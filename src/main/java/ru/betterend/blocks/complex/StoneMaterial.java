package ru.betterend.blocks.complex;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MaterialColor;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.ItemTags;
import ru.betterend.blocks.EndPedestal;
import ru.betterend.blocks.basis.BlockBase;
import ru.betterend.blocks.basis.BlockPillar;
import ru.betterend.blocks.basis.BlockSlab;
import ru.betterend.blocks.basis.BlockStairs;
import ru.betterend.blocks.basis.BlockStoneButton;
import ru.betterend.blocks.basis.BlockStonePressurePlate;
import ru.betterend.blocks.basis.BlockWall;
import ru.betterend.recipe.CraftingRecipes;
import ru.betterend.recipe.builders.GridRecipe;
import ru.betterend.registry.EndBlocks;
import ru.betterend.util.TagHelper;

public class StoneMaterial {
	public final Block stone;
	
	public final Block polished;
	public final Block tiles;
	public final Block pillar;
	public final Block stairs;
	public final Block slab;
	public final Block wall;
	public final Block button;
	public final Block pressure_plate;
	public final Block pedestal;
	
	public final Block bricks;
	public final Block brick_stairs;
	public final Block brick_slab;
	public final Block brick_wall;
	
	public StoneMaterial(String name, MaterialColor color) {
		FabricBlockSettings material = FabricBlockSettings.copyOf(Blocks.END_STONE).materialColor(color);
		
		stone = EndBlocks.registerBlock(name, new BlockBase(material));
		polished = EndBlocks.registerBlock(name + "_polished", new BlockBase(material));
		tiles = EndBlocks.registerBlock(name + "_tiles", new BlockBase(material));
		pillar = EndBlocks.registerBlock(name + "_pillar", new BlockPillar(material));
		stairs = EndBlocks.registerBlock(name + "_stairs", new BlockStairs(stone));
		slab = EndBlocks.registerBlock(name + "_slab", new BlockSlab(stone));
		wall = EndBlocks.registerBlock(name + "_wall", new BlockWall(stone));
		button = EndBlocks.registerBlock(name + "_button", new BlockStoneButton(stone));
		pressure_plate = EndBlocks.registerBlock(name + "_plate", new BlockStonePressurePlate(stone));
		pedestal = EndBlocks.registerBlock(name + "_pedestal", new EndPedestal(stone));
		
		bricks = EndBlocks.registerBlock(name + "_bricks", new BlockBase(material));
		brick_stairs = EndBlocks.registerBlock(name + "_bricks_stairs", new BlockStairs(bricks));
		brick_slab = EndBlocks.registerBlock(name + "_bricks_slab", new BlockSlab(bricks));
		brick_wall = EndBlocks.registerBlock(name + "_bricks_wall", new BlockWall(bricks));
		
		// Recipes //
		GridRecipe.make(name + "_bricks", bricks).setOutputCount(4).setShape("##", "##").addMaterial('#', stone).setGroup("end_bricks").build();
		GridRecipe.make(name + "_polished", polished).setOutputCount(4).setShape("##", "##").addMaterial('#', bricks).setGroup("end_tile").build();
		GridRecipe.make(name + "_tiles", tiles).setOutputCount(4).setShape("##", "##").addMaterial('#', polished).setGroup("end_small_tile").build();
		GridRecipe.make(name + "_pillar", pillar).setShape("#", "#").addMaterial('#', slab).setGroup("end_pillar").build();
		
		GridRecipe.make(name + "_stairs", stairs).setOutputCount(4).setShape("#  ", "## ", "###").addMaterial('#', stone).setGroup("end_stone_stairs").build();
		GridRecipe.make(name + "_slab", slab).setOutputCount(6).setShape("###").addMaterial('#', stone).setGroup("end_stone_slabs").build();
		GridRecipe.make(name + "_bricks_stairs", brick_stairs).setOutputCount(4).setShape("#  ", "## ", "###").addMaterial('#', bricks).setGroup("end_stone_stairs").build();
		GridRecipe.make(name + "_bricks_slab", brick_slab).setOutputCount(6).setShape("###").addMaterial('#', bricks).setGroup("end_stone_slabs").build();
		
		GridRecipe.make(name + "_wall", wall).setOutputCount(6).setShape("###", "###").addMaterial('#', stone).setGroup("end_wall").build();
		GridRecipe.make(name + "_bricks_wall", brick_wall).setOutputCount(6).setShape("###", "###").addMaterial('#', bricks).setGroup("end_wall").build();
		
		GridRecipe.make(name + "_button", button).setList("#").addMaterial('#', stone).setGroup("end_stone_buttons").build();
		GridRecipe.make(name + "_pressure_plate", pressure_plate).setShape("##").addMaterial('#', stone).setGroup("end_stone_plates").build();
		
		CraftingRecipes.registerPedestal(name + "_pedestal", pedestal, slab, pillar);
		
		// Item Tags //
		TagHelper.addTag(ItemTags.SLABS, slab, brick_slab);
		TagHelper.addTag(ItemTags.STONE_BRICKS, bricks);
		TagHelper.addTag(ItemTags.STONE_CRAFTING_MATERIALS, stone);
		TagHelper.addTag(ItemTags.STONE_TOOL_MATERIALS, stone);
		
		// Block Tags //
		TagHelper.addTag(BlockTags.STONE_BRICKS, bricks);
		TagHelper.addTag(BlockTags.WALLS, wall, brick_wall);
		TagHelper.addTag(BlockTags.SLABS, slab, brick_slab);
		TagHelper.addTags(pressure_plate, BlockTags.PRESSURE_PLATES, BlockTags.STONE_PRESSURE_PLATES);
	}
}