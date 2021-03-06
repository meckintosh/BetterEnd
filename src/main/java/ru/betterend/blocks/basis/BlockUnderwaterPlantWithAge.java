package ru.betterend.blocks.basis;

import java.util.Random;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;

public abstract class BlockUnderwaterPlantWithAge extends BlockUnderwaterPlant {
	public static final IntProperty AGE = IntProperty.of("age", 0, 3);
	
	public BlockUnderwaterPlantWithAge() {
		super(FabricBlockSettings.of(Material.UNDERWATER_PLANT)
				.breakByTool(FabricToolTags.SHEARS)
				.sounds(BlockSoundGroup.WET_GRASS)
				.breakByHand(true)
				.ticksRandomly()
				.noCollision());
	}
	
	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
		stateManager.add(AGE);
	}
	
	public abstract void grow(StructureWorldAccess world, Random random, BlockPos pos);
	
	@Override
	public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
		if (random.nextInt(4) == 0) {
			int age = state.get(AGE);
			if (age < 3) {
				world.setBlockState(pos, state.with(AGE, age + 1));
			}
			else {
				grow(world, random, pos);
			}
		}
	}
	
	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		super.scheduledTick(state, world, pos, random);
		if (canGrow(world, random, pos, state)) {
			grow(world, random, pos, state);
		}
	}
}
