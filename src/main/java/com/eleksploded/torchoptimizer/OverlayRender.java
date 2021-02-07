package com.eleksploded.torchoptimizer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nonnull;
import java.util.ArrayList;

public class OverlayRender {
	
	private ResourceLocation texture;
	private double[] texureMinX, texureMaxX;
	private double[] texureMinY, texureMaxY;
	
	public void switchTexture(int textureInt) {
		if (!TorchConfig.GENERAL.Optimize.get()) {
			texture = new ResourceLocation("torchoptimizer", "textures/overlay.png");
		} else {
			texture = new ResourceLocation("torchoptimizer", "textures/"+TorchOptimizer.value+".png");
		}
		
	}

	public OverlayRender() {
		switchTexture(2);
		texureMinX = new double[64];
		texureMaxX = new double[64];
		texureMinY = new double[64];
		texureMaxY = new double[64];
		for (int i = 0; i < 64; i++) {
			texureMinX[i] = (i % 8) / 8.0;
			texureMaxX[i] = (i % 8 + 1) / 8.0;
			texureMinY[i] = (i / 8) / 8.0;
			texureMaxY[i] = (i / 8 + 1) / 8.0;
		}
	}
 
	public void render(@Nonnull MatrixStack matrixStack, @Nonnull IRenderTypeBuffer renderer, ArrayList<Overlay>[][] overlays) {
		Vector3d projectedView = Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView();
		IVertexBuilder builder = renderer.getBuffer(OverlayRenderType.overlayRenderer(texture));
		matrixStack.translate(-projectedView.x, -projectedView.y, -projectedView.z);
		Matrix4f matrix4f = matrixStack.getLast().getMatrix();
		for (int i = 0; i < overlays.length; i++) {
			for (int j = 0; j < overlays[i].length; j++) {
				for (Overlay u : overlays[i][j]) {
					builder.pos(matrix4f, u.x, (float) (u.y), u.z).color(255, 255, 255, 255).tex((float) texureMinX[u.index], (float) texureMinY[u.index]).endVertex();
					builder.pos(matrix4f, u.x, (float) (u.y), u.z + 1).color(255, 255, 255, 255).tex((float) texureMinX[u.index], (float) texureMaxY[u.index]).endVertex();
					builder.pos(matrix4f, u.x + 1, (float) (u.y), u.z + 1).color(255, 255, 255, 255).tex((float) texureMaxX[u.index], (float) texureMaxY[u.index]).endVertex();
					builder.pos(matrix4f, u.x + 1, (float) (u.y), u.z).color(255, 255, 255, 255).tex((float) texureMaxX[u.index], (float) texureMinY[u.index]).endVertex();
				}
			}
		}

		/**
		//y=y-(double)1.9;
		TextureManager tm = Minecraft.getInstance().textureManager;
		// VertexBuffer
		tm.bindTexture(texture);
		BufferBuilder vb = Tessellator.getInstance().getBuffer();
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glPushMatrix();
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_DST_COLOR, GL11.GL_ZERO);
		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
		matrixStack.translate(-x, -y, -z);
//		vb.setTranslation(-x, -y, -z);
		for (int i = 0; i < overlays.length; i++)
			for (int j = 0; j < overlays[i].length; j++) {
				for (Overlay u : overlays[i][j]) {
					vb.pos(u.x, u.y-1.9D, u.z).tex((float)texureMinX[u.index], (float)texureMinY[u.index]).color(255, 255, 255, 255)
							.endVertex();
					vb.pos(u.x, u.y-1.9D, u.z + 1).tex((float)texureMinX[u.index], (float)texureMaxY[u.index]).color(255, 255, 255, 255)
							.endVertex();
					vb.pos(u.x + 1, u.y-1.9D, u.z + 1).tex((float)texureMaxX[u.index], (float)texureMaxY[u.index])
							.color(255, 255, 255, 255).endVertex();
					vb.pos(u.x + 1, u.y-1.9D, u.z).tex((float)texureMaxX[u.index], (float)texureMinY[u.index]).color(255, 255, 255, 255)
							.endVertex();
				}
			}
		matrixStack.translate(0,0,0);
//		vb.setTranslation(0, 0, 0);
		Tessellator.getInstance().draw();
		GL11.glPopMatrix();
		GL11.glPopAttrib();
		*/
	}
}