package ru.betterend.world.features;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import ru.betterend.noise.OpenSimplexNoise;
import ru.betterend.registry.EndBlocks;
import ru.betterend.registry.EndTags;
import ru.betterend.util.BlocksHelper;
import ru.betterend.util.MHelper;

public class EndLakeFeature extends DefaultFeature {
	private static final BlockState END_STONE = Blocks.END_STONE.getDefaultState();
	private static final OpenSimplexNoise NOISE = new OpenSimplexNoise(15152);
	private static final Mutable POS = new Mutable();
	
	@Override
	public boolean generate(StructureWorldAccess world, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, DefaultFeatureConfig featureConfig) {
		double radius = MHelper.randRange(10.0, 20.0, random);
		double depth = radius * 0.5 * MHelper.randRange(0.8, 1.2, random);
		int dist = MHelper.floor(radius);
		int dist2 = MHelper.floor(radius * 1.5);
		int bott = MHelper.floor(depth);
		blockPos = getPosOnSurfaceWG(world, blockPos);
		if (blockPos.getY() < 10) return false;
		
		int waterLevel = blockPos.getY();
		
		BlockPos pos = getPosOnSurfaceRaycast(world, blockPos.north(dist).up(10), 20);
		if (Math.abs(blockPos.getY() - pos.getY()) > 5) return false;
		waterLevel = MHelper.min(pos.getY(), waterLevel);
		
		pos = getPosOnSurfaceRaycast(world, blockPos.south(dist).up(10), 20);
		if (Math.abs(blockPos.getY() - pos.getY()) > 5) return false;
		waterLevel = MHelper.min(pos.getY(), waterLevel);
		
		pos = getPosOnSurfaceRaycast(world, blockPos.east(dist).up(10), 20);
		if (Math.abs(blockPos.getY() - pos.getY()) > 5) return false;
		waterLevel = MHelper.min(pos.getY(), waterLevel);
		
		pos = getPosOnSurfaceRaycast(world, blockPos.west(dist).up(10), 20);
		if (Math.abs(blockPos.getY() - pos.getY()) > 5) return false;
		waterLevel = MHelper.min(pos.getY(), waterLevel);
		BlockState state;
		
		int minX = blockPos.getX() - dist2;
		int maxX = blockPos.getX() + dist2;
		int minZ = blockPos.getZ() - dist2;
		int maxZ = blockPos.getZ() + dist2;
		int maskMinX = minX - 1;
		int maskMinZ = minZ - 1;
		
		boolean[][] mask = new boolean[maxX - minX + 3][maxZ - minZ + 3];
		for (int x = minX; x <= maxX; x++) {
			POS.setX(x);
			int mx = x - maskMinX;
			for (int z = minZ; z <= maxZ; z++) {
				POS.setZ(z);
				int mz = z - maskMinZ;
				if (!mask[mx][mz]) {
					for (int y = waterLevel + 1; y <= waterLevel + 20; y++) {
						POS.setY(y);
						FluidState fluid = world.getFluidState(POS);
						if (!fluid.isEmpty()) {
							for (int i = -1; i < 2; i++) {
								int px = mx + i;
								for (int j = -1; j < 2; j++) {
									int pz = mz + j;
									mask[px][pz] = true;
								}
							}
							break;
						}
					}
				}
			}
		}
		
		for (int x = minX; x <= maxX; x++) {
			POS.setX(x);
			int x2 = x - blockPos.getX();
			x2 *= x2;
			int mx = x - maskMinX;
			for (int z = minZ; z <= maxZ; z++) {
				POS.setZ(z);
				int z2 = z - blockPos.getZ();
				z2 *= z2;
				int mz = z - maskMinZ;
				if (!mask[mx][mz]) {
					double size = 1;
					for (int y = blockPos.getY(); y <= blockPos.getY() + 20; y++) {
						POS.setY(y);
						double add = y - blockPos.getY();
						if (add > 5) {
							size *= 0.8;
							add = 5;
						}
						double r = (add * 1.8 + radius * (NOISE.eval(x * 0.2, y * 0.2, z * 0.2) * 0.25 + 0.75)) - 1.0 / size;
						if (r > 0) {
							r *= r;
							if (x2 + z2 <= r) {
								state = world.getBlockState(POS);
								if (state.isIn(EndTags.GEN_TERRAIN)) {
									BlocksHelper.setWithoutUpdate(world, POS, AIR);
								}
								pos = POS.down();
								if (world.getBlockState(pos).isIn(EndTags.GEN_TERRAIN)) {
									state = world.getBiome(pos).getGenerationSettings().getSurfaceConfig().getTopMaterial();
									if (y > waterLevel + 1)
										BlocksHelper.setWithoutUpdate(world, pos, state);
									else if (y > waterLevel)
										BlocksHelper.setWithoutUpdate(world, pos, random.nextBoolean() ? state : EndBlocks.ENDSTONE_DUST.getDefaultState());
									else
										BlocksHelper.setWithoutUpdate(world, pos, EndBlocks.ENDSTONE_DUST.getDefaultState());
								}
							}
						}
						else {
							break;
						}
					}
				}
			}
		}
		
		double aspect = ((double) radius / (double) depth);

		for (int x = blockPos.getX() - dist; x <= blockPos.getX() + dist; x++) {
			POS.setX(x);
			int x2 = x - blockPos.getX();
			x2 *= x2;
			int mx = x - maskMinX;
			for (int z = blockPos.getZ() - dist; z <= blockPos.getZ() + dist; z++) {
				POS.setZ(z);
				int z2 = z - blockPos.getZ();
				z2 *= z2;
				int mz = z - maskMinZ;
				if (!mask[mx][mz]) {
					for (int y = blockPos.getY() - bott; y < blockPos.getY(); y++) {
						POS.setY(y);
						double y2 = (double) (y - blockPos.getY()) * aspect;
						y2 *= y2;
						double r = radius * (NOISE.eval(x * 0.2, y * 0.2, z * 0.2) * 0.25 + 0.75);
						double rb = r * 1.2;
						r *= r;
						rb *= rb;
						if (y2 + x2 + z2 <= r) {
							state = world.getBlockState(POS);
							if (canReplace(state)) {
								state = world.getBlockState(POS.up());
								state = canReplace(state) ? (y < waterLevel ? WATER : AIR) : state;
								BlocksHelper.setWithoutUpdate(world, POS, state);
								/*if (y == waterLevel - 1 && !state.getFluidState().isEmpty()) {
									world.getFluidTickScheduler().schedule(POS, state.getFluidState().getFluid(), 0);
								}*/
							}
							pos = POS.down();
							if (world.getBlockState(pos).getBlock().isIn(EndTags.GEN_TERRAIN)) {
								BlocksHelper.setWithoutUpdate(world, POS.down(), EndBlocks.ENDSTONE_DUST.getDefaultState());
							}
							pos = POS.up();
							while (canReplace(state = world.getBlockState(pos)) && !state.isAir() && state.getFluidState().isEmpty()) {
								BlocksHelper.setWithoutUpdate(world, pos, pos.getY() < waterLevel ? WATER : AIR);
								/*if (y == waterLevel - 1) {
									world.getFluidTickScheduler().schedule(POS, WATER.getFluidState().getFluid(), 0);
								}*/
								pos = pos.up();
							}
						}
						// Make border
						else if (y < waterLevel && y2 + x2 + z2 <= rb) {
							//if (world.getBlockState(POS).getMaterial().isReplaceable()) {
							if (world.isAir(POS.up())) {
								state = world.getBiome(POS).getGenerationSettings().getSurfaceConfig().getTopMaterial();
								BlocksHelper.setWithoutUpdate(world, POS, random.nextBoolean() ? state : EndBlocks.ENDSTONE_DUST.getDefaultState());
								BlocksHelper.setWithoutUpdate(world, POS.down(), END_STONE);
							}
							else {
								BlocksHelper.setWithoutUpdate(world, POS, EndBlocks.ENDSTONE_DUST.getDefaultState());
								BlocksHelper.setWithoutUpdate(world, POS.down(), END_STONE);
							}
							//}
						}
					}
				}
			}
		}
		
		BlocksHelper.fixBlocks(world, new BlockPos(minX - 2, waterLevel - 2, minZ - 2), new BlockPos(maxX + 2, blockPos.getY() + 20, maxZ + 2));
		
		return true;
	}
	
	/*private void generateFoggyMushroomland(WorldAccess world, int x, int z, int waterLevel) {
		if (NOISE.eval(x * 0.1, z * 0.1, 0) > 0) {
			BlocksHelper.setWithoutUpdate(world, POS, BlockRegistry.BUBBLE_CORAL.getDefaultState());
		}
		else if (NOISE.eval(x * 0.1, z * 0.1, 1) > 0) {
			BlocksHelper.setWithoutUpdate(world, POS, BlockRegistry.END_LILY.getDefaultState().with(BlockEndLily.SHAPE, TripleShape.BOTTOM));
			BlockPos up = POS.up();
			while (up.getY() < waterLevel) {
				BlocksHelper.setWithoutUpdate(world, up, BlockRegistry.END_LILY.getDefaultState().with(BlockEndLily.SHAPE, TripleShape.MIDDLE));
				up = up.up();
			}
			BlocksHelper.setWithoutUpdate(world, up, BlockRegistry.END_LILY.getDefaultState().with(BlockEndLily.SHAPE, TripleShape.TOP));
		}
	}*/
	
	private boolean canReplace(BlockState state) {
		return state.getMaterial().isReplaceable()
				|| state.isIn(EndTags.GEN_TERRAIN)
				|| state.isOf(EndBlocks.ENDSTONE_DUST)
				|| state.getMaterial().equals(Material.PLANT)
				|| state.getMaterial().equals(Material.UNDERWATER_PLANT)
				|| state.getMaterial().equals(Material.UNUSED_PLANT);
	}
}
