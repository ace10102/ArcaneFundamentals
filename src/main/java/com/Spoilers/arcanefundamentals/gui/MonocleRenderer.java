package com.Spoilers.arcanefundamentals.gui;

import java.util.Optional;

import org.apache.commons.lang3.tuple.ImmutableTriple;

import com.Spoilers.arcanefundamentals.config.AFClientConfig;
import com.Spoilers.arcanefundamentals.items.AFItems;
import com.Spoilers.arcanefundamentals.util.AFRenderTypes;
import com.ma.api.ManaAndArtificeMod;
import com.ma.api.capabilities.IPlayerMagic;
import com.ma.api.capabilities.WellspringNode;

import top.theillusivec4.curios.api.CuriosApi;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class MonocleRenderer {

    private final Minecraft mc = Minecraft.getInstance();

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {

        if (!Minecraft.renderNames())
            return;

        ClientPlayerEntity player = mc.player;
        Optional<ImmutableTriple<String, Integer, ItemStack>> equipped = CuriosApi.getCuriosHelper().findEquippedCurio(AFItems.ELDRIN_MONOCLE.get(), player);
        if (!equipped.isPresent() || !((ImmutableTriple<String, Integer, ItemStack>)equipped.get()).getRight().getItem().equals(AFItems.ELDRIN_MONOCLE.get())) {
            return;
        }
        IPlayerMagic magic = (IPlayerMagic)player.getCapability(ManaAndArtificeMod.getMagicCapability()).orElse(null);
        if (magic == null || !magic.isMagicUnlocked()) {
            return;
        }
        Effect eldrin = ForgeRegistries.POTIONS.getValue(new ResourceLocation("mana-and-artifice:eldrin-sight"));
        Effect wellspring = ForgeRegistries.POTIONS.getValue(new ResourceLocation("mana-and-artifice:wellspring-sight"));
        boolean effect = player.hasEffect(eldrin) || player.hasEffect(wellspring);

        ActiveRenderInfo renderInfo = mc.gameRenderer.getMainCamera();
        Entity cameraEntity = renderInfo.getEntity() != null ? renderInfo.getEntity() : mc.player;
        ClientWorld client = mc.level;
        client.getCapability(ManaAndArtificeMod.getWorldMagicCapability()).ifPresent(m -> m.getWellspringRegistry().getNearbyNodes(player.blockPosition(), 0, 32).forEach((pos, node) -> {

            renderManaBar(mc, event.getMatrixStack(), event.getPartialTicks(), renderInfo, cameraEntity, pos, node, effect);

        }));
    }

    public void renderManaBar(Minecraft mc, MatrixStack matrixStack, float partialTicks, ActiveRenderInfo renderInfo, Entity viewPoint, BlockPos wellspringPos, WellspringNode node, boolean effect) {

        EntityRendererManager renderManager = mc.getEntityRenderDispatcher();
        Vector3d renderPos = renderManager.camera.getPosition();

        matrixStack.pushPose();
        if (node.hasForcedYLevel()) 
            matrixStack.translate(wellspringPos.getX() + 0.5F - renderPos.x(), node.getYLevel() - 1.4F - renderPos.y(), wellspringPos.getZ() + 0.5F - renderPos.z());
        else 
            matrixStack.translate(wellspringPos.getX() + 0.5F - renderPos.x(), (1 / renderPos.y()) - 0.2F, wellspringPos.getZ() + 0.5F - renderPos.z());

        IRenderTypeBuffer.Impl buffer = mc.renderBuffers().bufferSource();
        renderElement(mc, matrixStack, buffer, renderInfo, node.getStrength(), effect, node.hasForcedYLevel());
        matrixStack.popPose();
    }

    private void renderElement(Minecraft mc, MatrixStack matrixStack, IRenderTypeBuffer.Impl buffer, ActiveRenderInfo renderInfo, float strength, boolean effect, boolean locked) {
        // rotate to follow camera
        Quaternion rotation = renderInfo.rotation().copy();
        // rotation.mul(-1F); //no idea what the point of this is but neat had it so it might do something
        matrixStack.mulPose(rotation);

        // setup for texture panel
        final float scale = 1f;
        float size = AFClientConfig.ELEMENT_SIZE.get().floatValue();// config
        float offsetY = AFClientConfig.ELEMENT_HEIGHT.get().floatValue() * 4;// config
        float offsetX = -AFClientConfig.ELEMENT_OFFSET.get().floatValue();// config
        int light = 0xF000F0;
        float alpha = effect || locked ? 0.8F : 0.05F;

        // texture panel matrix prep i guess
        matrixStack.scale(-scale, -scale / 4, scale);
        MatrixStack.Entry entry = matrixStack.last();
        Matrix4f modelViewMatrix = entry.pose();
        Vector3f normal = new Vector3f(0.0F, 1.0F, 0.0F);
        normal.transform(entry.normal());

        IVertexBuilder builder = buffer.getBuffer(AFRenderTypes.getManaBarType(AFRenderTypes.MANA_BAR_TEXTURE));

        // Background texture panel
        builder.vertex(modelViewMatrix, -size - offsetX, -size - offsetY, 0.001F).uv(0F, 0F).normal(normal.x(), normal.y(), normal.z()).uv2(light).color(1F, 1F, 1F, alpha).endVertex();
        builder.vertex(modelViewMatrix, -size - offsetX, size - offsetY, 0.001F).uv(0F, 1F).normal(normal.x(), normal.y(), normal.z()).uv2(light).color(1F, 1F, 1F, alpha).endVertex();
        builder.vertex(modelViewMatrix, size - offsetX, size - offsetY, 0.001F).uv(1F, 1F).normal(normal.x(), normal.y(), normal.z()).uv2(light).color(1F, 1F, 1F, alpha).endVertex();
        builder.vertex(modelViewMatrix, size - offsetX, -size - offsetY, 0.001F).uv(1F, 0F).normal(normal.x(), normal.y(), normal.z()).uv2(light).color(1F, 1F, 1F, alpha).endVertex();

        // setup for relational text translation
        String power = String.valueOf(strength);// "000/000";
        float textLength = mc.font.width(power);
        float factor = (textLength / 2 < 18 ? 24 : 2 * textLength / 3) / size;
        float textScale = (1 / (factor));
        float textTall = mc.font.lineHeight;
        int white = 0xFFFFFF;
        int black = 0x000000;
        int transparency = effect || locked ? 0 : (16 << 24);

        // text matrix setup
        matrixStack.scale(textScale, textScale * 4, textScale);
        modelViewMatrix = matrixStack.last().pose();
        matrixStack.pushPose();

        // text positioning
        float textY = (float)(0.3 - (offsetY * (factor / 4)) - (textTall / 2.5F));// YHeight;
        float textX = (float)(0.575 - (offsetX * factor) - (textLength / 2));// XOffset

        // text render
        mc.font.drawInBatch(power, textX, textY, (white + transparency), false, modelViewMatrix, buffer, false, black, light);

        matrixStack.popPose();
    }
}