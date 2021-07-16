package com.Spoilers.arcanefundamentals.util;

import com.Spoilers.arcanefundamentals.ArcaneFundamentals;
import com.google.common.collect.ImmutableList;

import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.ResourceLocation;

import static net.minecraft.client.renderer.vertex.DefaultVertexFormats.*;

public class AFRenderType extends RenderState {
	
	public static final ResourceLocation MANA_BAR_TEXTURE = new ResourceLocation(ArcaneFundamentals.MOD_ID, "textures/gui/mana_value_bar.png");

	public static final VertexFormat POSITION_TEX_COLOR_NORMAL_LIGHTMAP = new VertexFormat(ImmutableList.<VertexFormatElement>builder().add(ELEMENT_POSITION).add(ELEMENT_UV0).add(ELEMENT_COLOR).add(ELEMENT_NORMAL).add(ELEMENT_UV2).add(ELEMENT_PADDING).build());
	
	public AFRenderType(String nameIn, Runnable setupTaskIn, Runnable clearTaskIn) {
		super(nameIn, setupTaskIn, clearTaskIn);
	}
	
	public static RenderType getManaBarType(ResourceLocation location) {
		RenderType.State renderTypeState = RenderType.State.builder().setTextureState(new TextureState(location, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setDiffuseLightingState(RenderState.NO_DIFFUSE_LIGHTING).setLightmapState(LIGHTMAP).createCompositeState(true);
		return RenderType.create("af_mana_bar", POSITION_TEX_COLOR_NORMAL_LIGHTMAP, 7, 256, true, true, renderTypeState);
	}
}
