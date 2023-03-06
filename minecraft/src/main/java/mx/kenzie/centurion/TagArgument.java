package mx.kenzie.centurion;

import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TagArgument<Type extends Keyed> extends HashedArg<Tag<Type>> {
    private static final Map<String, Tag<Material>> MATERIALS = new HashMap<>();
    private static final Map<String, Tag<Material>> ITEMS = new HashMap<>();
    private static final Map<String, Tag<EntityType>> ENTITIES = new HashMap<>();

    static {
        if (Bukkit.getServer() != null) {
            MATERIALS.put("WOOL", Tag.WOOL);
            MATERIALS.put("PLANKS", Tag.PLANKS);
            MATERIALS.put("STONE_BRICKS", Tag.STONE_BRICKS);
            MATERIALS.put("WOODEN_BUTTONS", Tag.WOODEN_BUTTONS);
            MATERIALS.put("BUTTONS", Tag.BUTTONS);
            MATERIALS.put("WOOL_CARPETS", Tag.WOOL_CARPETS);
            MATERIALS.put("CARPETS", Tag.CARPETS);
            MATERIALS.put("WOODEN_DOORS", Tag.WOODEN_DOORS);
            MATERIALS.put("WOODEN_STAIRS", Tag.WOODEN_STAIRS);
            MATERIALS.put("WOODEN_SLABS", Tag.WOODEN_SLABS);
            MATERIALS.put("WOODEN_FENCES", Tag.WOODEN_FENCES);
            MATERIALS.put("PRESSURE_PLATES", Tag.PRESSURE_PLATES);
            MATERIALS.put("WOODEN_PRESSURE_PLATES", Tag.WOODEN_PRESSURE_PLATES);
            MATERIALS.put("STONE_PRESSURE_PLATES", Tag.STONE_PRESSURE_PLATES);
            MATERIALS.put("WOODEN_TRAPDOORS", Tag.WOODEN_TRAPDOORS);
            MATERIALS.put("DOORS", Tag.DOORS);
            MATERIALS.put("SAPLINGS", Tag.SAPLINGS);
            MATERIALS.put("LOGS_THAT_BURN", Tag.LOGS_THAT_BURN);
            MATERIALS.put("LOGS", Tag.LOGS);
            MATERIALS.put("DARK_OAK_LOGS", Tag.DARK_OAK_LOGS);
            MATERIALS.put("OAK_LOGS", Tag.OAK_LOGS);
            MATERIALS.put("BIRCH_LOGS", Tag.BIRCH_LOGS);
            MATERIALS.put("ACACIA_LOGS", Tag.ACACIA_LOGS);
            MATERIALS.put("JUNGLE_LOGS", Tag.JUNGLE_LOGS);
            MATERIALS.put("SPRUCE_LOGS", Tag.SPRUCE_LOGS);
            MATERIALS.put("MANGROVE_LOGS", Tag.MANGROVE_LOGS);
            MATERIALS.put("CRIMSON_STEMS", Tag.CRIMSON_STEMS);
            MATERIALS.put("WARPED_STEMS", Tag.WARPED_STEMS);
            MATERIALS.put("BAMBOO_BLOCKS", Tag.BAMBOO_BLOCKS);
            MATERIALS.put("BANNERS", Tag.BANNERS);
            MATERIALS.put("SAND", Tag.SAND);
            MATERIALS.put("STAIRS", Tag.STAIRS);
            MATERIALS.put("SLABS", Tag.SLABS);
            MATERIALS.put("WALLS", Tag.WALLS);
            MATERIALS.put("ANVIL", Tag.ANVIL);
            MATERIALS.put("RAILS", Tag.RAILS);
            MATERIALS.put("LEAVES", Tag.LEAVES);
            MATERIALS.put("TRAPDOORS", Tag.TRAPDOORS);
            MATERIALS.put("FLOWER_POTS", Tag.FLOWER_POTS);
            MATERIALS.put("SMALL_FLOWERS", Tag.SMALL_FLOWERS);
            MATERIALS.put("BEDS", Tag.BEDS);
            MATERIALS.put("FENCES", Tag.FENCES);
            MATERIALS.put("TALL_FLOWERS", Tag.TALL_FLOWERS);
            MATERIALS.put("FLOWERS", Tag.FLOWERS);
            MATERIALS.put("PIGLIN_REPELLENTS", Tag.PIGLIN_REPELLENTS);
            MATERIALS.put("GOLD_ORES", Tag.GOLD_ORES);
            MATERIALS.put("IRON_ORES", Tag.IRON_ORES);
            MATERIALS.put("DIAMOND_ORES", Tag.DIAMOND_ORES);
            MATERIALS.put("REDSTONE_ORES", Tag.REDSTONE_ORES);
            MATERIALS.put("LAPIS_ORES", Tag.LAPIS_ORES);
            MATERIALS.put("COAL_ORES", Tag.COAL_ORES);
            MATERIALS.put("EMERALD_ORES", Tag.EMERALD_ORES);
            MATERIALS.put("COPPER_ORES", Tag.COPPER_ORES);
            MATERIALS.put("CANDLES", Tag.CANDLES);
            MATERIALS.put("DIRT", Tag.DIRT);
            MATERIALS.put("TERRACOTTA", Tag.TERRACOTTA);
            MATERIALS.put("COMPLETES_FIND_TREE_TUTORIAL", Tag.COMPLETES_FIND_TREE_TUTORIAL);
            MATERIALS.put("ENDERMAN_HOLDABLE", Tag.ENDERMAN_HOLDABLE);
            MATERIALS.put("ICE", Tag.ICE);
            MATERIALS.put("VALID_SPAWN", Tag.VALID_SPAWN);
            MATERIALS.put("IMPERMEABLE", Tag.IMPERMEABLE);
            MATERIALS.put("UNDERWATER_BONEMEALS", Tag.UNDERWATER_BONEMEALS);
            MATERIALS.put("CORAL_BLOCKS", Tag.CORAL_BLOCKS);
            MATERIALS.put("WALL_CORALS", Tag.WALL_CORALS);
            MATERIALS.put("CORAL_PLANTS", Tag.CORAL_PLANTS);
            MATERIALS.put("CORALS", Tag.CORALS);
            MATERIALS.put("BAMBOO_PLANTABLE_ON", Tag.BAMBOO_PLANTABLE_ON);
            MATERIALS.put("STANDING_SIGNS", Tag.STANDING_SIGNS);
            MATERIALS.put("WALL_SIGNS", Tag.WALL_SIGNS);
            MATERIALS.put("SIGNS", Tag.SIGNS);
            MATERIALS.put("CEILING_HANGING_SIGNS", Tag.CEILING_HANGING_SIGNS);
            MATERIALS.put("WALL_HANGING_SIGNS", Tag.WALL_HANGING_SIGNS);
            MATERIALS.put("ALL_HANGING_SIGNS", Tag.ALL_HANGING_SIGNS);
            MATERIALS.put("ALL_SIGNS", Tag.ALL_SIGNS);
            MATERIALS.put("DRAGON_IMMUNE", Tag.DRAGON_IMMUNE);
            MATERIALS.put("DRAGON_TRANSPARENT", Tag.DRAGON_TRANSPARENT);
            MATERIALS.put("WITHER_IMMUNE", Tag.WITHER_IMMUNE);
            MATERIALS.put("WITHER_SUMMON_BASE_BLOCKS", Tag.WITHER_SUMMON_BASE_BLOCKS);
            MATERIALS.put("BEEHIVES", Tag.BEEHIVES);
            MATERIALS.put("CROPS", Tag.CROPS);
            MATERIALS.put("BEE_GROWABLES", Tag.BEE_GROWABLES);
            MATERIALS.put("PORTALS", Tag.PORTALS);
            MATERIALS.put("FIRE", Tag.FIRE);
            MATERIALS.put("NYLIUM", Tag.NYLIUM);
            MATERIALS.put("WART_BLOCKS", Tag.WART_BLOCKS);
            MATERIALS.put("BEACON_BASE_BLOCKS", Tag.BEACON_BASE_BLOCKS);
            MATERIALS.put("SOUL_SPEED_BLOCKS", Tag.SOUL_SPEED_BLOCKS);
            MATERIALS.put("WALL_POST_OVERRIDE", Tag.WALL_POST_OVERRIDE);
            MATERIALS.put("CLIMBABLE", Tag.CLIMBABLE);
            MATERIALS.put("FALL_DAMAGE_RESETTING", Tag.FALL_DAMAGE_RESETTING);
            MATERIALS.put("SHULKER_BOXES", Tag.SHULKER_BOXES);
            MATERIALS.put("HOGLIN_REPELLENTS", Tag.HOGLIN_REPELLENTS);
            MATERIALS.put("SOUL_FIRE_BASE_BLOCKS", Tag.SOUL_FIRE_BASE_BLOCKS);
            MATERIALS.put("STRIDER_WARM_BLOCKS", Tag.STRIDER_WARM_BLOCKS);
            MATERIALS.put("CAMPFIRES", Tag.CAMPFIRES);
            MATERIALS.put("GUARDED_BY_PIGLINS", Tag.GUARDED_BY_PIGLINS);
            MATERIALS.put("PREVENT_MOB_SPAWNING_INSIDE", Tag.PREVENT_MOB_SPAWNING_INSIDE);
            MATERIALS.put("FENCE_GATES", Tag.FENCE_GATES);
            MATERIALS.put("UNSTABLE_BOTTOM_CENTER", Tag.UNSTABLE_BOTTOM_CENTER);
            MATERIALS.put("MUSHROOM_GROW_BLOCK", Tag.MUSHROOM_GROW_BLOCK);
            MATERIALS.put("INFINIBURN_OVERWORLD", Tag.INFINIBURN_OVERWORLD);
            MATERIALS.put("INFINIBURN_NETHER", Tag.INFINIBURN_NETHER);
            MATERIALS.put("INFINIBURN_END", Tag.INFINIBURN_END);
            MATERIALS.put("BASE_STONE_OVERWORLD", Tag.BASE_STONE_OVERWORLD);
            MATERIALS.put("STONE_ORE_REPLACEABLES", Tag.STONE_ORE_REPLACEABLES);
            MATERIALS.put("DEEPSLATE_ORE_REPLACEABLES", Tag.DEEPSLATE_ORE_REPLACEABLES);
            MATERIALS.put("BASE_STONE_NETHER", Tag.BASE_STONE_NETHER);
            MATERIALS.put("OVERWORLD_CARVER_REPLACEABLES", Tag.OVERWORLD_CARVER_REPLACEABLES);
            MATERIALS.put("NETHER_CARVER_REPLACEABLES", Tag.NETHER_CARVER_REPLACEABLES);
            MATERIALS.put("CANDLE_CAKES", Tag.CANDLE_CAKES);
            MATERIALS.put("CAULDRONS", Tag.CAULDRONS);
            MATERIALS.put("CRYSTAL_SOUND_BLOCKS", Tag.CRYSTAL_SOUND_BLOCKS);
            MATERIALS.put("INSIDE_STEP_SOUND_BLOCKS", Tag.INSIDE_STEP_SOUND_BLOCKS);
            MATERIALS.put("OCCLUDES_VIBRATION_SIGNALS", Tag.OCCLUDES_VIBRATION_SIGNALS);
            MATERIALS.put("DAMPENS_VIBRATIONS", Tag.DAMPENS_VIBRATIONS);
            MATERIALS.put("DRIPSTONE_REPLACEABLE", Tag.DRIPSTONE_REPLACEABLE);
            MATERIALS.put("CAVE_VINES", Tag.CAVE_VINES);
            MATERIALS.put("MOSS_REPLACEABLE", Tag.MOSS_REPLACEABLE);
            MATERIALS.put("LUSH_GROUND_REPLACEABLE", Tag.LUSH_GROUND_REPLACEABLE);
            MATERIALS.put("AZALEA_ROOT_REPLACEABLE", Tag.AZALEA_ROOT_REPLACEABLE);
            MATERIALS.put("SMALL_DRIPLEAF_PLACEABLE", Tag.SMALL_DRIPLEAF_PLACEABLE);
            MATERIALS.put("BIG_DRIPLEAF_PLACEABLE", Tag.BIG_DRIPLEAF_PLACEABLE);
            MATERIALS.put("SNOW", Tag.SNOW);
            MATERIALS.put("MINEABLE_AXE", Tag.MINEABLE_AXE);
            MATERIALS.put("MINEABLE_HOE", Tag.MINEABLE_HOE);
            MATERIALS.put("MINEABLE_PICKAXE", Tag.MINEABLE_PICKAXE);
            MATERIALS.put("MINEABLE_SHOVEL", Tag.MINEABLE_SHOVEL);
            MATERIALS.put("NEEDS_DIAMOND_TOOL", Tag.NEEDS_DIAMOND_TOOL);
            MATERIALS.put("NEEDS_IRON_TOOL", Tag.NEEDS_IRON_TOOL);
            MATERIALS.put("NEEDS_STONE_TOOL", Tag.NEEDS_STONE_TOOL);
            MATERIALS.put("FEATURES_CANNOT_REPLACE", Tag.FEATURES_CANNOT_REPLACE);
            MATERIALS.put("LAVA_POOL_STONE_CANNOT_REPLACE", Tag.LAVA_POOL_STONE_CANNOT_REPLACE);
            MATERIALS.put("GEODE_INVALID_BLOCKS", Tag.GEODE_INVALID_BLOCKS);
            MATERIALS.put("FROG_PREFER_JUMP_TO", Tag.FROG_PREFER_JUMP_TO);
            MATERIALS.put("SCULK_REPLACEABLE", Tag.SCULK_REPLACEABLE);
            MATERIALS.put("SCULK_REPLACEABLE_WORLD_GEN", Tag.SCULK_REPLACEABLE_WORLD_GEN);
            MATERIALS.put("ANCIENT_CITY_REPLACEABLE", Tag.ANCIENT_CITY_REPLACEABLE);
            MATERIALS.put("ANIMALS_SPAWNABLE_ON", Tag.ANIMALS_SPAWNABLE_ON);
            MATERIALS.put("AXOLOTLS_SPAWNABLE_ON", Tag.AXOLOTLS_SPAWNABLE_ON);
            MATERIALS.put("GOATS_SPAWNABLE_ON", Tag.GOATS_SPAWNABLE_ON);
            MATERIALS.put("MOOSHROOMS_SPAWNABLE_ON", Tag.MOOSHROOMS_SPAWNABLE_ON);
            MATERIALS.put("PARROTS_SPAWNABLE_ON", Tag.PARROTS_SPAWNABLE_ON);
            MATERIALS.put("POLAR_BEARS_SPAWNABLE_ON_ALTERNATE", Tag.POLAR_BEARS_SPAWNABLE_ON_ALTERNATE);
            MATERIALS.put("POLAR_BEARS_SPAWNABLE_ON_IN_FROZEN_OCEAN", Tag.POLAR_BEARS_SPAWNABLE_ON_IN_FROZEN_OCEAN);
            MATERIALS.put("RABBITS_SPAWNABLE_ON", Tag.RABBITS_SPAWNABLE_ON);
            MATERIALS.put("FOXES_SPAWNABLE_ON", Tag.FOXES_SPAWNABLE_ON);
            MATERIALS.put("WOLVES_SPAWNABLE_ON", Tag.WOLVES_SPAWNABLE_ON);
            MATERIALS.put("FROGS_SPAWNABLE_ON", Tag.FROGS_SPAWNABLE_ON);
            MATERIALS.put("AZALEA_GROWS_ON", Tag.AZALEA_GROWS_ON);
            MATERIALS.put("REPLACEABLE_PLANTS", Tag.REPLACEABLE_PLANTS);
            MATERIALS.put("CONVERTABLE_TO_MUD", Tag.CONVERTABLE_TO_MUD);
            MATERIALS.put("MANGROVE_LOGS_CAN_GROW_THROUGH", Tag.MANGROVE_LOGS_CAN_GROW_THROUGH);
            MATERIALS.put("MANGROVE_ROOTS_CAN_GROW_THROUGH", Tag.MANGROVE_ROOTS_CAN_GROW_THROUGH);
            MATERIALS.put("DEAD_BUSH_MAY_PLACE_ON", Tag.DEAD_BUSH_MAY_PLACE_ON);
            MATERIALS.put("SNAPS_GOAT_HORN", Tag.SNAPS_GOAT_HORN);
            MATERIALS.put("SNOW_LAYER_CANNOT_SURVIVE_ON", Tag.SNOW_LAYER_CANNOT_SURVIVE_ON);
            MATERIALS.put("SNOW_LAYER_CAN_SURVIVE_ON", Tag.SNOW_LAYER_CAN_SURVIVE_ON);
            MATERIALS.put("INVALID_SPAWN_INSIDE", Tag.INVALID_SPAWN_INSIDE);
            // items
            ITEMS.put("PIGLIN_LOVED", Tag.ITEMS_PIGLIN_LOVED);
            ITEMS.put("IGNORED_BY_PIGLIN_BABIES", Tag.IGNORED_BY_PIGLIN_BABIES);
            ITEMS.put("PIGLIN_FOOD", Tag.PIGLIN_FOOD);
            ITEMS.put("FOX_FOOD", Tag.FOX_FOOD);
            ITEMS.put("BANNERS", Tag.ITEMS_BANNERS);
            ITEMS.put("BOATS", Tag.ITEMS_BOATS);
            ITEMS.put("CHEST_BOATS", Tag.ITEMS_CHEST_BOATS);
            ITEMS.put("NON_FLAMMABLE_WOOD", Tag.ITEMS_NON_FLAMMABLE_WOOD);
            ITEMS.put("FISHES", Tag.ITEMS_FISHES);
            ITEMS.put("MUSIC_DISCS", Tag.ITEMS_MUSIC_DISCS);
            ITEMS.put("CREEPER_DROP_MUSIC_DISCS", Tag.ITEMS_CREEPER_DROP_MUSIC_DISCS);
            ITEMS.put("COALS", Tag.ITEMS_COALS);
            ITEMS.put("ARROWS", Tag.ITEMS_ARROWS);
            ITEMS.put("LECTERN_BOOKS", Tag.ITEMS_LECTERN_BOOKS);
            ITEMS.put("BOOKSHELF_BOOKS", Tag.ITEMS_BOOKSHELF_BOOKS);
            ITEMS.put("BEACON_PAYMENT_ITEMS", Tag.ITEMS_BEACON_PAYMENT_ITEMS);
            ITEMS.put("STONE_TOOL_MATERIALS", Tag.ITEMS_STONE_TOOL_MATERIALS);
            ITEMS.put("FURNACE_MATERIALS", Tag.ITEMS_FURNACE_MATERIALS);
            ITEMS.put("COMPASSES", Tag.ITEMS_COMPASSES);
            ITEMS.put("HANGING_SIGNS", Tag.ITEMS_HANGING_SIGNS);
            ITEMS.put("CREEPER_IGNITERS", Tag.ITEMS_CREEPER_IGNITERS);
            ITEMS.put("FREEZE_IMMUNE_WEARABLES", Tag.FREEZE_IMMUNE_WEARABLES);
            ITEMS.put("AXOLOTL_TEMPT_ITEMS", Tag.AXOLOTL_TEMPT_ITEMS);
            ITEMS.put("CLUSTER_MAX_HARVESTABLES", Tag.CLUSTER_MAX_HARVESTABLES);
            // entities
            ENTITIES.put("SKELETONS", Tag.ENTITY_TYPES_SKELETONS);
            ENTITIES.put("RAIDERS", Tag.ENTITY_TYPES_RAIDERS);
            ENTITIES.put("BEEHIVE_INHABITORS", Tag.ENTITY_TYPES_BEEHIVE_INHABITORS);
            ENTITIES.put("ARROWS", Tag.ENTITY_TYPES_ARROWS);
            ENTITIES.put("IMPACT_PROJECTILES", Tag.ENTITY_TYPES_IMPACT_PROJECTILES);
            ENTITIES.put("POWDER_SNOW_WALKABLE_MOBS", Tag.ENTITY_TYPES_POWDER_SNOW_WALKABLE_MOBS);
            ENTITIES.put("AXOLOTL_ALWAYS_HOSTILES", Tag.ENTITY_TYPES_AXOLOTL_ALWAYS_HOSTILES);
            ENTITIES.put("AXOLOTL_HUNT_TARGETS", Tag.ENTITY_TYPES_AXOLOTL_HUNT_TARGETS);
            ENTITIES.put("FREEZE_IMMUNE_ENTITY_TYPES", Tag.ENTITY_TYPES_FREEZE_IMMUNE_ENTITY_TYPES);
            ENTITIES.put("FREEZE_HURTS_EXTRA_TYPES", Tag.ENTITY_TYPES_FREEZE_HURTS_EXTRA_TYPES);
            ENTITIES.put("FROG_FOOD", Tag.ENTITY_TYPES_FROG_FOOD);
        }
    }

    protected final Map<String, Tag<Type>> map;

    @SuppressWarnings("unchecked")
    public TagArgument(Class<Type> type, Map<String, Tag<Type>> map) {
        super((Class<Tag<Type>>) (Class) Tag.class);
        this.map = map;
        this.label = type.getSimpleName().toLowerCase() + "s";
    }

    public static TagArgument<Material> materials() {
        return new TagArgument<>(Material.class, MATERIALS);
    }

    public static TagArgument<Material> items() {
        return new TagArgument<>(Material.class, ITEMS);
    }

    public static TagArgument<EntityType> entities() {
        return new TagArgument<>(EntityType.class, ENTITIES);
    }

    @Override
    public Tag<Type> parseNew(String input) {
        return lastValue = map.get(input.substring(1).toUpperCase());
    }

    @Override
    public String[] possibilities() {
        if (possibilities != null && possibilities.length > 0) return possibilities;
        final List<String> list = new ArrayList<>(map.size());
        for (String key : map.keySet()) list.add("#" + key.toLowerCase());
        return possibilities = list.toArray(new String[0]);
    }

    @Override
    public boolean matches(String input) {
        if (input.charAt(0) != '#') return false;
        this.lastHash = input.hashCode();
        this.lastValue = null;
        return this.parseNew(input) != null;
    }

}
