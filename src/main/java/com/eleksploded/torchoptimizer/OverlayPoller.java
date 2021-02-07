package com.eleksploded.torchoptimizer;

import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.Chunk;

import java.util.ArrayList;

public class OverlayPoller extends Thread {

	public volatile ArrayList<Overlay>[][] overlays;

	public void run() {
		int radius = 0;
		while (true) {
			int chunkRadius = updateChunkRadius();
			radius = radius % chunkRadius + 1;
			if (TorchOptimizer.instance.active)
				updateLightLevel(radius, chunkRadius);
			try {
				sleep(TorchConfig.GENERAL.pollingInterval.get());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("unchecked")
	private int updateChunkRadius() {
		int size = TorchConfig.GENERAL.chunkRadius.get();
		if (overlays == null || overlays.length != size * 2 + 1) {
			overlays = new ArrayList[size * 2 + 1][size * 2 + 1];
			for (int i = 0; i < overlays.length; i++)
				for (int j = 0; j < overlays[i].length; j++)
					overlays[i][j] = new ArrayList<Overlay>();
		}
		return size;
	}

	@SuppressWarnings("deprecation")
	private void updateLightLevel(int radius, int chunkRadius) {

		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null)
			return;

		ClientWorld world = mc.world;
		int playerPosY = (int) Math.floor(mc.player.getPosY());
		int playerChunkX = mc.player.chunkCoordX;
		int playerChunkZ = mc.player.chunkCoordZ;
		int skyLightSub = world.getSkylightSubtracted();

		for (int chunkX = playerChunkX - radius; chunkX <= playerChunkX + radius; chunkX++)
			for (int chunkZ = playerChunkZ - radius; chunkZ <= playerChunkZ + radius; chunkZ++) {
				Chunk chunk = mc.world.getChunk(chunkX, chunkZ);
				if (!world.isAreaLoaded(new BlockPos(chunk.getPos().getXStart(),70,chunk.getPos().getZStart()), new BlockPos(chunk.getPos().getXEnd(),70,chunk.getPos().getZEnd())))
					continue;
				ArrayList<Overlay> buffer = new ArrayList<Overlay>();
				for (int offsetX = 0; offsetX < 16; offsetX++)
					for (int offsetZ = 0; offsetZ < 16; offsetZ++) {
						int posX = (chunkX << 4) + offsetX;
						int posZ = (chunkZ << 4) + offsetZ;
						int maxY = playerPosY + 4, minY = Math.max(playerPosY - 40, 0);
						BlockState preBlockState = null, curBlockState = chunk.getBlockState(new BlockPos(offsetX, maxY, offsetZ));
						Block preBlock = null, curBlock = curBlockState.getBlock();
						BlockPos prePos = null, curPos = new BlockPos(posX, maxY, posZ);
						for (int posY = maxY - 1; posY >= minY; posY--) {
							preBlockState = curBlockState;
							curBlockState = chunk.getBlockState(new BlockPos(offsetX, posY, offsetZ));
							preBlock = curBlock;
							curBlock = curBlockState.getBlock();
							prePos = curPos;
							curPos = new BlockPos(posX, posY, posZ);
							if (curBlock == Blocks.AIR || curBlock == Blocks.BEDROCK || curBlock == Blocks.BARRIER
									|| preBlockState.isSolid() || preBlockState.getMaterial().isLiquid()
									|| preBlockState.canProvidePower()
									|| curBlockState.isSolidSide(world, curPos, Direction.UP) == false //doesSideBlockRendering
									|| AbstractRailBlock.isRail(preBlockState)
									|| world.getBiomeManager().getBiome(curPos).toString().contains("shroom")) { //|| chunk.getBiome(curPos).toString().contains("shroom")
								continue;
							}
							double offsetY = 0;
							if (preBlock == Blocks.SNOW || isCarpet(preBlock)) {
								offsetY = preBlockState.getOffset(world, curPos).y;
								if (offsetY >= 0.15)
									continue; // Snow layer too high
							}
							int blockLight = world.getLightFor(LightType.BLOCK, prePos);
							if (blockLight >= 8 && blockLight < 24)
								blockLight ^= 16;
							buffer.add(new Overlay(posX, posY + offsetY + 1, posZ, blockLight));
						}
					}
				int len = chunkRadius * 2 + 1;
				int arrayX = (chunkX % len + len) % len;
				int arrayZ = (chunkZ % len + len) % len;
				overlays[arrayX][arrayZ] = buffer;
			}

	}

	private boolean isCarpet(Block b) {
		return b.isIn(BlockTags.CARPETS);
	}

}
