package com.Spoilers.arcanefundamentals.gui;

import java.util.Optional;
import java.util.Random;

import org.apache.commons.lang3.tuple.ImmutableTriple;

import com.Spoilers.arcanefundamentals.items.AFItems;
import com.Spoilers.arcanefundamentals.util.AFRenderTypes;
import com.ma.api.ManaAndArtificeMod;
import com.ma.api.capabilities.IPlayerMagic;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.blaze3d.vertex.VertexBuilderUtils;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.curios.api.CuriosApi;

public class CandleRenderer {

    private final Minecraft mc = Minecraft.getInstance();

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {

        ClientPlayerEntity player = mc.player;
        Optional<ImmutableTriple<String, Integer, ItemStack>> equipped = CuriosApi.getCuriosHelper().findEquippedCurio(AFItems.MANA_MONOCLE.get(), player);
        if (!equipped.isPresent() || !(equipped.get()).getRight().getItem().equals(AFItems.MANA_MONOCLE.get()) || equipped.get().getRight().getTag() == null) {
            return;
        }
        IPlayerMagic magic = (IPlayerMagic) player.getCapability(ManaAndArtificeMod.getMagicCapability()).orElse(null);
        if (magic == null || !magic.isMagicUnlocked() || !equipped.get().getRight().getTag().contains("location")) {
            return;
        }

        MatrixStack matrixStack = event.getMatrixStack();
        IRenderTypeBuffer.Impl buffer = mc.renderBuffers().bufferSource();
        Vector3d cameraPos = mc.gameRenderer.getMainCamera().getPosition();

        BlockPos candlePos = BlockPos.of(equipped.get().getRight().getTag().getLong("location"));
        AxisAlignedBB candleArea = new AxisAlignedBB(candlePos.offset(new Vector3i(-32, -32, -32)), candlePos.offset(new Vector3i(33, 33, 33)));

        matrixStack.pushPose();
        matrixStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        if (cameraPos.closerThan(candleArea.getCenter(), 160d))
            if (candleArea.contains(cameraPos))
                WorldRenderer.renderLineBox(matrixStack, buffer.getBuffer(AFRenderTypes.CANDLE_RENDERER), new AxisAlignedBB(candlePos), 0f, 1f, 0.5f, 1f);
            else
                WorldRenderer.renderLineBox(matrixStack, buffer.getBuffer(AFRenderTypes.CANDLE_RENDERER), new AxisAlignedBB(candlePos), 1f, 0f, 0.5f, 1f);
        if (cameraPos.closerThan(candleArea.getCenter(), 192d))
            WorldRenderer.renderLineBox(matrixStack, buffer.getBuffer(RenderType.lines()), candleArea, 0f, 0f, 0.4f, 1f);

        RenderType rendertype = Atlases.translucentCullBlockSheet();//seems to have been the default render type returned by many layers of logic
        IVertexBuilder ivertexbuilder = VertexBuilderUtils.create(buffer.getBuffer(RenderType.glintDirect()), buffer.getBuffer(rendertype));//bind glint render type to block render type
        IBakedModel ibakedmodel = mc.getBlockRenderer().getBlockModel(Blocks.ICE.defaultBlockState());//any solid block
        int light = mc.getEntityRenderDispatcher().getPackedLightCoords(player, event.getPartialTicks());

        matrixStack.translate(candlePos.getX() - 31.98, candlePos.getY() - 31.98, candlePos.getZ() - 31.98);
        matrixStack.scale(64.96F, 64.96F, 64.96F);//micro offsets to prevent zfighting
        MatrixStack.Entry matrixstack$entry = matrixStack.last();

        Random random = new Random();

        if (cameraPos.closerThan(candleArea.getCenter(), 192d)) {
            for (Direction direction : Direction.values()) {
                random.setSeed(42L);
                for (BakedQuad bakedquad : ibakedmodel.getQuads((BlockState)null, direction, random)) {
                    ivertexbuilder.addVertexData(matrixstack$entry, bakedquad, 1, 1, 1, 0.02f, light, OverlayTexture.NO_OVERLAY, true);//set alpha stupid low to stop texture rendering
                }
            }
            //itemRenderer.renderStatic(itemStack, ItemCameraTransforms.TransformType.FIXED, light, OverlayTexture.NO_OVERLAY, matrixStack, buffer);
            //original render code for reference
        }
        matrixStack.popPose();
    }
}