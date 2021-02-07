package com.eleksploded.TorchOptimizer;

import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class OverlayRenderType extends RenderType {
    public OverlayRenderType(String nameIn, VertexFormat formatIn, int drawModeIn, int bufferSizeIn, boolean useDelegateIn, boolean needsSortingIn, Runnable setupTaskIn, Runnable clearTaskIn) {
        super(nameIn, formatIn, drawModeIn, bufferSizeIn, useDelegateIn, needsSortingIn, setupTaskIn, clearTaskIn);
    }

    public static RenderType overlayRenderer(ResourceLocation resourceLocation) {
        RenderType.State state = RenderType.State.getBuilder()
                .texture(new RenderState.TextureState(resourceLocation, false, false))//Texture state
                .lightmap(LIGHTMAP_DISABLED)//disableLighting
                .cull(CULL_ENABLED)//enableCull
                .alpha(HALF_ALPHA)//alpha
                .build(true);
        return makeType("torchoptimizer:overlay_renderer", DefaultVertexFormats.POSITION_COLOR_TEX, GL11.GL_QUADS, 256, true, false, state);
    }
}
