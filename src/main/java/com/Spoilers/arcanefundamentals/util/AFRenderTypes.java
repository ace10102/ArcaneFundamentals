package com.Spoilers.arcanefundamentals.util;

import com.Spoilers.arcanefundamentals.ArcaneFundamentals;
import com.google.common.collect.ImmutableList;

import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.ResourceLocation;

import static net.minecraft.client.renderer.vertex.DefaultVertexFormats.*;

import java.util.OptionalDouble;

public class AFRenderTypes extends RenderType {

    public static final ResourceLocation MANA_BAR_TEXTURE = new ResourceLocation(ArcaneFundamentals.MOD_ID, "textures/gui/mana_value_bar.png");
    public static final ResourceLocation PORTAL_TEXTURE_RAID = new ResourceLocation("mana-and-artifice", "textures/entity/vortex_raid.png");

    public static final VertexFormat POSITION_TEX_NORMAL_LIGHTMAP = new VertexFormat(ImmutableList.<VertexFormatElement>builder().add(ELEMENT_POSITION).add(ELEMENT_UV0).add(ELEMENT_NORMAL).add(ELEMENT_UV2).add(ELEMENT_PADDING).add(ELEMENT_COLOR).build());
    
    public static final RenderType CANDLE_RENDERER = AFRenderTypes.create("candle_area_render", DefaultVertexFormats.POSITION_COLOR, 1, 256, true, true, RenderType.State.builder().setLineState(new RenderState.LineState(OptionalDouble.empty())).setLayeringState(NO_LAYERING).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setOutputState(OUTLINE_TARGET).setWriteMaskState(COLOR_WRITE).setDepthTestState(NO_DEPTH_TEST).createCompositeState(false));
    public static final RenderType AFGLINT_DIRECT = AFRenderTypes.create("afglint_direct", DefaultVertexFormats.POSITION_TEX, 7, 256, RenderType.State.builder().setTextureState(new RenderState.TextureState(ItemRenderer.ENCHANT_GLINT_LOCATION, true, false)).setWriteMaskState(COLOR_WRITE).setCullState(NO_CULL).setDepthTestState(EQUAL_DEPTH_TEST).setTransparencyState(GLINT_TRANSPARENCY).setTexturingState(GLINT_TEXTURING).createCompositeState(false));
    public static final RenderType RADIANT_RENDER_TYPE = AFRenderTypes.create("radiant", DefaultVertexFormats.POSITION_COLOR, 7, 256, false, true, RenderType.State.builder().setWriteMaskState(COLOR_WRITE).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setOutputState(PARTICLES_TARGET).setShadeModelState(SMOOTH_SHADE).setCullState(CULL).setDepthTestState(LEQUAL_DEPTH_TEST).createCompositeState(false));
    public static final RenderType RAID_PORTAL_HANDLER_RENDER = AFRenderTypes.create("portal_render_type", DefaultVertexFormats.NEW_ENTITY, 7, 256, true, true, RenderType.State.builder().setTextureState(new RenderState.TextureState(PORTAL_TEXTURE_RAID, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setWriteMaskState(COLOR_DEPTH_WRITE).setDiffuseLightingState(DIFFUSE_LIGHTING).setAlphaState(DEFAULT_ALPHA).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true));

    public AFRenderTypes(String nameIn, VertexFormat format, int something1, int somethin2, boolean something3, boolean something4, Runnable setupTaskIn, Runnable clearTaskIn) {
        super(nameIn, format, something1, somethin2, something3, something4, setupTaskIn, clearTaskIn);
    }

    public static RenderType getManaBarType(ResourceLocation location) {
        RenderType.State renderTypeState = RenderType.State.builder().setTextureState(new TextureState(location, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setDiffuseLightingState(RenderState.NO_DIFFUSE_LIGHTING).setLightmapState(LIGHTMAP).createCompositeState(true);
        return RenderType.create("af_mana_bar", POSITION_TEX_NORMAL_LIGHTMAP, 7, 256, true, true, renderTypeState);
    }
}
