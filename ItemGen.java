package com.snoop787.testplugin2;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

/**
 * Created by Chris Turner on 11/19/2014, last modified on 11/20/2014.
 *
 * Credit to soulofw0lf for design inspiration and guidance.
 * All rights reserved.
 */

public class ItemGen {

    private enum ItemType {
        WEAPON,
        ARMOR,
        TOOL
    }

    // Comment this out and replace with CustomMobsType.r
    private Random r;

    private String bestEnchantName;
    private int bestEnchantLevel = 0;
    private ItemType rndIType;

    // Generates a COMPLETELY RANDOM item (weapon, tool, or armor), complete with possibility of
    // having enchantments. The Item is named based on the material and type selected. Enchantment
    // presence also has an effect on the name.
    //
    //   For example: A randomly generated item could be:
    //      &1Blacksmith's Hatchet of the Dwarf
    //
    //   with the following stats:
    //      Iron Axe (Tool)
    //      Unbreaking 1, Haste 2
    //
    public ItemStack getRandomItem()
    {
        r = new Random(System.currentTimeMillis());

        int rnd = r.nextInt(33);

        Material m = getRandomMaterial(rnd);
        ItemType type = rndIType;

        ItemStack is = new ItemStack(m, 1);
        ItemMeta im = is.getItemMeta();

        // Get the name and lore
        String name = nameItem(m);
        List<String> lore = im.getLore();

        // Get map of enchantments (determines rarity)
        Map<Enchantment, Integer> enchMap = getRandomEnchantments(m, r.nextInt(100), type);

        String rarityFormat = getRarityFormat(enchMap.size());

        // Add enchantments as unsafe enchantments; could be a problem, but limit is 3 levels for an enchant.
        if (enchMap.size() > 0)
            is.addUnsafeEnchantments(enchMap);

        String suffix = generateItemSuffix();

        // Add and set lore of the item
        lore.add(Misc.color(rarityFormat + name + suffix));

        im.setLore(lore);
        is.setItemMeta(im);

        // Return completed random item
        return is;
    }

    // Generates a random weapon, tool, or armor, complete with possibility of having enchantments.
    // Item is named according to its material (m) and its item type (ItemType.TOOL, ItemType.ARMOR, or
    // ItemType.WEAPON). Enchantment presence also has an effect on the name.
    //
    //   For example: A randomly generated item of material IRON_AXE and type ItemType.TOOL could be:
    //      &1Blacksmith's Hatchet of the Dwarf
    //
    //   with the following stats:
    //      Iron Axe
    //      Unbreaking 1, Haste 2
    //
    public ItemStack getRandomItem(Material m, ItemType type)
    {
        ItemStack is = new ItemStack(m, 1);
        ItemMeta im = is.getItemMeta();

        r = new Random(System.currentTimeMillis());

        // Get the name and lore
        String name = nameItem(m);
        List<String> lore = im.getLore();

        // Get map of enchantments (determines rarity)
        Map<Enchantment, Integer> enchMap = getRandomEnchantments(m, r.nextInt(100), type);

        String rarityFormat = getRarityFormat(enchMap.size());

        // Add enchantments as unsafe enchantments; could be a problem, but limit is 3 levels for an enchant.
        if (enchMap.size() > 0)
            is.addUnsafeEnchantments(enchMap);

        String suffix = generateItemSuffix();

        // Add and set lore of the item
        lore.add(Misc.color(rarityFormat + name + suffix));

        im.setLore(lore);
        is.setItemMeta(im);

        // Return completed random item
        return is;
    }

    private Map<Enchantment, Integer> getRandomEnchantments(Material m, int chance, ItemType it) {
        Map<Enchantment, Integer> enchMap = new HashMap<>();
        int howMany;

        // Determine how many enchantments to grant
        if (chance <= 70)
            howMany = 0;
        else if (chance > 70 && chance <= 85)
            howMany = 1;
        else if (chance > 85 && chance <= 95)
            howMany = 2;
        else
            howMany = 3;

        //  Get the enchantments (if there are more then 0 to choose)
        if (howMany > 0) {
            for( int i = 0; i < howMany; i++ ) {
                Enchantment ench = null;

                switch (it) {
                    case WEAPON:
                        if (m == Material.BOW)
                            ench = getEnchantForWeapon(true);
                        else
                            ench = getEnchantForWeapon(false);
                        break;
                    case TOOL:
                        ench = getEnchantForTool();
                        break;
                    case ARMOR:
                        ench = getEnchantForArmor();
                        break;
                }

                int eLvl = r.nextInt(3)+1;
                if (eLvl > bestEnchantLevel)
                {
                    bestEnchantLevel = eLvl;
                    bestEnchantName = ench.getName();
                }

                if (!enchMap.containsKey(ench))
                    enchMap.put(ench, eLvl);
                else
                    i--;
            }
        }

        return enchMap;
    }

    private String getRarityFormat(int n) {
        String str;

        switch (n)
        {
            case 0:
                str = "&f";
                break;
            case 1:
                str = "&2";
                break;
            case 2:
                str = "&1";
                break;
            case 3:
                str = "&5";
                break;
            default:
                str = "&f";
                break;
        }

        return str;
    }

    private Enchantment getEnchantForWeapon(boolean isBow) {
        List<Enchantment> wpnEnchList = new ArrayList<>();

        if (isBow) {
            wpnEnchList.add(Enchantment.ARROW_DAMAGE);
            wpnEnchList.add(Enchantment.ARROW_FIRE);
            wpnEnchList.add(Enchantment.ARROW_INFINITE);
            wpnEnchList.add(Enchantment.ARROW_KNOCKBACK);
            wpnEnchList.add(Enchantment.DURABILITY);
        }
        else {
            wpnEnchList.add(Enchantment.DAMAGE_ALL);
            wpnEnchList.add(Enchantment.DAMAGE_ARTHROPODS);
            wpnEnchList.add(Enchantment.DAMAGE_UNDEAD);
            wpnEnchList.add(Enchantment.DURABILITY);
            wpnEnchList.add(Enchantment.FIRE_ASPECT);
            wpnEnchList.add(Enchantment.KNOCKBACK);
            wpnEnchList.add(Enchantment.LOOT_BONUS_MOBS);
        }

        Enchantment selected = wpnEnchList.get(r.nextInt(wpnEnchList.size()));

        return selected;
    }

    private Enchantment getEnchantForArmor() {
        List<Enchantment> armEnchList = new ArrayList<>();

        armEnchList.add(Enchantment.DURABILITY);
        armEnchList.add(Enchantment.OXYGEN);
        armEnchList.add(Enchantment.PROTECTION_ENVIRONMENTAL);
        armEnchList.add(Enchantment.PROTECTION_EXPLOSIONS);
        armEnchList.add(Enchantment.PROTECTION_FALL);
        armEnchList.add(Enchantment.PROTECTION_FIRE);
        armEnchList.add(Enchantment.PROTECTION_PROJECTILE);
        armEnchList.add(Enchantment.THORNS);

        Enchantment selected = armEnchList.get(r.nextInt(armEnchList.size()));

        return selected;
    }

    private Enchantment getEnchantForTool() {
        List<Enchantment> armEnchList = new ArrayList<>();

        armEnchList.add(Enchantment.DIG_SPEED);
        armEnchList.add(Enchantment.WATER_WORKER);
        armEnchList.add(Enchantment.LOOT_BONUS_BLOCKS);
        armEnchList.add(Enchantment.DURABILITY);

        Enchantment selected = armEnchList.get(r.nextInt(armEnchList.size()));

        return selected;
    }

    private String generateItemPrefix(String material) {
        int rdm = r.nextInt(9);
        String prefix = "";

        switch (material) {
            case "leather":
                switch (rdm) {
                    case 0:
                        prefix = "Leather";
                        break;
                    case 1:
                        prefix = "Tanned";
                        break;
                    case 2:
                        prefix = "Padded";
                        break;
                    case 3:
                        prefix = "Hide";
                        break;
                    case 4:
                        prefix = "Patchwork";
                        break;
                    case 5:
                        prefix = "Deadskin";
                        break;
                    case 6:
                        prefix = "Hardened Leather";
                        break;
                    case 7:
                        prefix = "Simple";
                        break;
                    case 8:
                        prefix = "Knave's";
                        break;
                    default:
                        break;
                }
            case "wood":
                switch (rdm) {
                    case 0:
                        prefix = "Wooden";
                        break;
                    case 1:
                        prefix = "Whittled";
                        break;
                    case 2:
                        prefix = "Carved";
                        break;
                    case 3:
                        prefix = "Gnarled";
                        break;
                    case 4:
                        prefix = "Aged";
                        break;
                    case 5:
                        prefix = "Weak";
                        break;
                    case 6:
                        prefix = "Withered";
                        break;
                    case 7:
                        prefix = "Old";
                        break;
                    case 8:
                        prefix = "Cheap";
                        break;
                    default:
                        break;
                }
            case "stone":
                switch (rdm) {
                    case 0:
                        prefix = "Stone";
                        break;
                    case 1:
                        prefix = "Flint";
                        break;
                    case 2:
                        prefix = "Crude";
                        break;
                    case 3:
                        prefix = "Hardened";
                        break;
                    case 4:
                        prefix = "Primitive";
                        break;
                    case 5:
                        prefix = "Chiseled";
                        break;
                    case 6:
                        prefix = "Beginner's";
                        break;
                    case 7:
                        prefix = "Petrified";
                        break;
                    case 8:
                        prefix = "Dull";
                        break;
                    default:
                        break;
                }
            case "iron":
                switch (rdm) {
                    case 0:
                        prefix = "Iron";
                        break;
                    case 1:
                        prefix = "Reinforced";
                        break;
                    case 2:
                        prefix = "Steel";
                        break;
                    case 3:
                        prefix = "Plated";
                        break;
                    case 4:
                        prefix = "Blacksmith's";
                        break;
                    case 5:
                        prefix = "Knight's";
                        break;
                    case 6:
                        prefix = "Footman's";
                        break;
                    case 7:
                        prefix = "Bound";
                        break;
                    case 8:
                        prefix = "Polished";
                        break;
                    default:
                        break;
                }
            case "gold":
                switch (rdm) {
                    case 0:
                        prefix = "Golden";
                        break;
                    case 1:
                        prefix = "Gilded";
                        break;
                    case 2:
                        prefix = "Shiny";
                        break;
                    case 3:
                        prefix = "Ritual";
                        break;
                    case 4:
                        prefix = "King's";
                        break;
                    case 5:
                        prefix = "Royal";
                        break;
                    case 6:
                        prefix = "Lavish";
                        break;
                    case 7:
                        prefix = "Opulent";
                        break;
                    case 8:
                        prefix = "Ceremonial";
                        break;
                    default:
                        break;
                }
            case "diamond":
                switch (rdm) {
                    case 0:
                        prefix = "Diamond";
                        break;
                    case 1:
                        prefix = "Bejeweled";
                        break;
                    case 2:
                        prefix = "Heavy";
                        break;
                    case 3:
                        prefix = "Pristine";
                        break;
                    case 4:
                        prefix = "Destroyer's";
                        break;
                    case 5:
                        prefix = "Glass";
                        break;
                    case 6:
                        prefix = "Husky's";
                        break;
                    case 7:
                        prefix = "Elegant";
                        break;
                    case 8:
                        prefix = "Otherworldly";
                        break;
                    default:
                        break;
                }
            default:
                break;
        }
        return prefix;
    }

    private String generateItemName(String type) {
        int rdm = r.nextInt(9);
        String prefix = "";

        switch (type) {
            case "sword":
                switch (rdm) {
                    case 0:
                        prefix = "Sword";
                        break;
                    case 1:
                        prefix = "Longsword";
                        break;
                    case 2:
                        prefix = "Broadsword";
                        break;
                    case 3:
                        prefix = "Greatsword";
                        break;
                    case 4:
                        prefix = "Kukri";
                        break;
                    case 5:
                        prefix = "Chopping Blade";
                        break;
                    case 6:
                        prefix = "Shiv";
                        break;
                    case 7:
                        prefix = "Dagger";
                        break;
                    case 8:
                        prefix = "Knife";
                        break;
                    default:
                        break;
                }
            case "axe":
                switch (rdm) {
                    case 0:
                        prefix = "Axe";
                        break;
                    case 1:
                        prefix = "Hatchet";
                        break;
                    case 2:
                        prefix = "Chopper";
                        break;
                    case 3:
                        prefix = "Tomahawk";
                        break;
                    case 4:
                        prefix = "Lumber Axe";
                        break;
                    case 5:
                        prefix = "Tree-Feller";
                        break;
                    case 6:
                        prefix = "Battle Axe";
                        break;
                    case 7:
                        prefix = "Heavy Mace";
                        break;
                    case 8:
                        prefix = "War Hammer";
                        break;
                    default:
                        break;
                }
            case "pickaxe":
                switch (rdm) {
                    case 0:
                        prefix = "Pickaxe";
                        break;
                    case 1:
                        prefix = "Excavator";
                        break;
                    case 2:
                        prefix = "Rock Breaker";
                        break;
                    case 3:
                        prefix = "Scythe";
                        break;
                    case 4:
                        prefix = "Sickle";
                        break;
                    case 5:
                        prefix = "Pick";
                        break;
                    case 6:
                        prefix = "Digger";
                        break;
                    case 7:
                        prefix = "Crusher";
                        break;
                    case 8:
                        prefix = "Fang";
                        break;
                    default:
                        break;
                }
            case "spade":
                switch (rdm) {
                    case 0:
                        prefix = "Spade";
                        break;
                    case 1:
                        prefix = "Shovel";
                        break;
                    case 2:
                        prefix = "Dirt Mover";
                        break;
                    case 3:
                        prefix = "Spear";
                        break;
                    case 4:
                        prefix = "Glaive";
                        break;
                    case 5:
                        prefix = "Giant's Spoon";
                        break;
                    case 6:
                        prefix = "Pilum";
                        break;
                    case 7:
                        prefix = "Gardener";
                        break;
                    case 8:
                        prefix = "Longspear";
                        break;
                    default:
                        break;
                }
            case "hoe":
                switch (rdm) {
                    case 0:
                        prefix = "Hoe";
                        break;
                    case 1:
                        prefix = "Trowel";
                        break;
                    case 2:
                        prefix = "Weeder";
                        break;
                    case 3:
                        prefix = "Edger";
                        break;
                    case 4:
                        prefix = "BR0-B4";
                        break;
                    case 5:
                        prefix = "Tiller";
                        break;
                    case 6:
                        prefix = "Rake";
                        break;
                    case 7:
                        prefix = "Farmstick";
                        break;
                    case 8:
                        prefix = "Grafter";
                        break;
                    default:
                        break;
                }
            case "bow":
                switch (rdm) {
                    case 0:
                        prefix = "Bow";
                        break;
                    case 1:
                        prefix = "Longbow";
                        break;
                    case 2:
                        prefix = "Short Bow";
                        break;
                    case 3:
                        prefix = "Dart Gun";
                        break;
                    case 4:
                        prefix = "Compound Bow";
                        break;
                    case 5:
                        prefix = "Recurve Bow";
                        break;
                    case 6:
                        prefix = "Arrow Lobber";
                        break;
                    case 7:
                        prefix = "Crossbow";
                        break;
                    case 8:
                        prefix = "Sling";
                        break;
                    default:
                        break;
                }
            case "chestplate":
                switch (rdm) {
                    case 0:
                        prefix = "Chestplate";
                        break;
                    case 1:
                        prefix = "Breastplate";
                        break;
                    case 2:
                        prefix = "Cuirasse";
                        break;
                    case 3:
                        prefix = "Deflector";
                        break;
                    case 4:
                        prefix = "Shirt";
                        break;
                    case 5:
                        prefix = "Jerkin";
                        break;
                    case 6:
                        prefix = "Gorget";
                        break;
                    case 7:
                        prefix = "Vest";
                        break;
                    case 8:
                        prefix = "Jacket";
                        break;
                    default:
                        break;
                }
            case "leggings":
                switch (rdm) {
                    case 0:
                        prefix = "Leggings";
                        break;
                    case 1:
                        prefix = "Overalls";
                        break;
                    case 2:
                        prefix = "Breeches";
                        break;
                    case 3:
                        prefix = "Waders";
                        break;
                    case 4:
                        prefix = "Trousers";
                        break;
                    case 5:
                        prefix = "Pant";
                        break;
                    case 6:
                        prefix = "Chausses";
                        break;
                    case 7:
                        prefix = "Pants";
                        break;
                    case 8:
                        prefix = "Legplates";
                        break;
                    default:
                        break;
                }
            case "helmet":
                switch (rdm) {
                    case 0:
                        prefix = "Helmet";
                        break;
                    case 1:
                        prefix = "Helm";
                        break;
                    case 2:
                        prefix = "Bucket Helm";
                        break;
                    case 3:
                        prefix = "Sallet";
                        break;
                    case 4:
                        prefix = "Skullcap";
                        break;
                    case 5:
                        prefix = "Hat";
                        break;
                    case 6:
                        prefix = "Fedora";
                        break;
                    case 7:
                        prefix = "Mask";
                        break;
                    case 8:
                        prefix = "Fez";
                        break;
                    default:
                        break;
                }
            case "boots":
                switch (rdm) {
                    case 0:
                        prefix = "Boots";
                        break;
                    case 1:
                        prefix = "Shoes";
                        break;
                    case 2:
                        prefix = "Sabatons";
                        break;
                    case 3:
                        prefix = "Greaves";
                        break;
                    case 4:
                        prefix = "Moccasins";
                        break;
                    case 5:
                        prefix = "Clogs";
                        break;
                    case 6:
                        prefix = "Shinguards";
                        break;
                    case 7:
                        prefix = "Footwear";
                        break;
                    case 8:
                        prefix = "Cleats";
                        break;
                    default:
                        break;
                }
            default:
                break;
        }
        return prefix;
    }

    private String generateItemSuffix() {
        String suffix = "";
        int rndNum = r.nextInt(5);

        switch (bestEnchantName.toLowerCase())
        {
            case "arrow_damage":
                switch(rndNum) {
                    case 0:
                        suffix = " of Piercing";
                        break;
                    case 1:
                        suffix = " of Marksmanship";
                        break;
                    case 2:
                        suffix = " of Wounding";
                        break;
                    case 3:
                        suffix = " of the Sniper";
                        break;
                    case 4:
                        suffix = " of Bullseyes";
                        break;
                    default:
                        break;
                }
                break;
            case "arrow_fire":
                switch(rndNum) {
                    case 0:
                        suffix = " of Flames";
                        break;
                    case 1:
                        suffix = " of Fiery Arrows";
                        break;
                    case 2:
                        suffix = " of Hellbolts";
                        break;
                    case 3:
                        suffix = " of Firestarting";
                        break;
                    case 4:
                        suffix = " of the Spark";
                        break;
                    default:
                        break;
                }
                break;
            case "arrow_infinite":
                switch(rndNum) {
                    case 0:
                        suffix = " of Infinity";
                        break;
                    case 1:
                        suffix = " the Neverending";
                        break;
                    case 2:
                        suffix = " of Volleys";
                        break;
                    case 3:
                        suffix = " of Deathly Rain";
                        break;
                    case 4:
                        suffix = " of the Elf";
                        break;
                    default:
                        break;
                }
                break;
            case "arrow_knockback":
                switch(rndNum) {
                    case 0:
                        suffix = " of Knockback";
                        break;
                    case 1:
                        suffix = " of the Piston";
                        break;
                    case 2:
                        suffix = " of Pushing";
                        break;
                    case 3:
                        suffix = " of Bullrushing";
                        break;
                    case 4:
                        suffix = " of Punches";
                        break;
                    default:
                        break;
                }
                break;
            case "damage_all":
                switch(rndNum) {
                    case 0:
                        suffix = " of Sundering";
                        break;
                    case 1:
                        suffix = " of the Gladiator";
                        break;
                    case 2:
                        suffix = " of Crushing";
                        break;
                    case 3:
                        suffix = " of Reaving";
                        break;
                    case 4:
                        suffix = " of Harming";
                        break;
                    default:
                        break;
                }
                break;
            case "damage_arthropods":
                switch(rndNum) {
                    case 0:
                        suffix = " of Spiderbane";
                        break;
                    case 1:
                        suffix = " of the Exterminator";
                        break;
                    case 2:
                        suffix = " of Bug-Crushing";
                        break;
                    case 3:
                        suffix = " of Eradication";
                        break;
                    case 4:
                        suffix = ", Chosen of The Boot";
                        break;
                    default:
                        break;
                }
                break;
            case "damage_undead":
                switch(rndNum) {
                    case 0:
                        suffix = " of Zombiebane";
                        break;
                    case 1:
                        suffix = " of the Exorcist";
                        break;
                    case 2:
                        suffix = " of Consecration";
                        break;
                    case 3:
                        suffix = ", the Holy Avenger";
                        break;
                    case 4:
                        suffix = " of Undead-slaying";
                        break;
                    default:
                        break;
                }
                break;
            case "dig_speed":
                switch(rndNum) {
                    case 0:
                        suffix = " of Haste";
                        break;
                    case 1:
                        suffix = " of the Dwarf";
                        break;
                    case 2:
                        suffix = " of Rock Sundering";
                        break;
                    case 3:
                        suffix = " of Fast Mining";
                        break;
                    case 4:
                        suffix = " of Rapid Strikes";
                        break;
                    default:
                        break;
                }
                break;
            case "durability":
                switch(rndNum) {
                    case 0:
                        suffix = " of Unbreaking";
                        break;
                    case 1:
                        suffix = " of the Shield";
                        break;
                    case 2:
                        suffix = " of Resistance";
                        break;
                    case 3:
                        suffix = " of Fortitude";
                        break;
                    case 4:
                        suffix = ", the Immovable Object";
                        break;
                    default:
                        break;
                }
                break;
            case "fire_aspect":
                switch(rndNum) {
                    case 0:
                        suffix = " of Flames";
                        break;
                    case 1:
                        suffix = " of the Salamander";
                        break;
                    case 2:
                        suffix = " of Ignition";
                        break;
                    case 3:
                        suffix = ", the Nethertouched";
                        break;
                    case 4:
                        suffix = " of Blazes";
                        break;
                    default:
                        break;
                }
                break;
            case "knockback":
                switch(rndNum) {
                    case 0:
                        suffix = " of the Piston";
                        break;
                    case 1:
                        suffix = " of Force";
                        break;
                    case 2:
                        suffix = ", the Unstoppable Force";
                        break;
                    case 3:
                        suffix = " of Charging";
                        break;
                    case 4:
                        suffix = " of Repelling";
                        break;
                    default:
                        break;
                }
                break;
            case "loot_bonus_blocks":
                switch(rndNum) {
                    case 0:
                        suffix = " of Luck";
                        break;
                    case 1:
                        suffix = " of the Treasure Seeker";
                        break;
                    case 2:
                        suffix = " of Fortune";
                        break;
                    case 3:
                        suffix = " of Avarice";
                        break;
                    case 4:
                        suffix = " of Greed";
                        break;
                    default:
                        break;
                }
                break;
            case "loot_bonus_mobs":
                switch(rndNum) {
                    case 0:
                        suffix = " of Looting";
                        break;
                    case 1:
                        suffix = " of the Thief";
                        break;
                    case 2:
                        suffix = " of Stealing";
                        break;
                    case 3:
                        suffix = " of Pickpocketing";
                        break;
                    case 4:
                        suffix = " of Coveting";
                        break;
                    default:
                        break;
                }
                break;
            case "oxygen":
                switch(rndNum) {
                    case 0:
                        suffix = " of Water-Breathing";
                        break;
                    case 1:
                        suffix = " of Flowing Air";
                        break;
                    case 2:
                        suffix = " of the Fish";
                        break;
                    case 3:
                        suffix = " of Extended Breath";
                        break;
                    case 4:
                        suffix = " of the Undersea";
                        break;
                    default:
                        break;
                }
                break;
            case "protection_environmental":
                switch(rndNum) {
                    case 0:
                        suffix = " of Barkshield";
                        break;
                    case 1:
                        suffix = " of the Bulwark";
                        break;
                    case 2:
                        suffix = " of Resistance";
                        break;
                    case 3:
                        suffix = " of Protection";
                        break;
                    case 4:
                        suffix = " of Defense";
                        break;
                    default:
                        break;
                }
                break;
            case "protection_explosions":
                switch(rndNum) {
                    case 0:
                        suffix = " of Explosive Resistance";
                        break;
                    case 1:
                        suffix = " of the Bunker";
                        break;
                    case 2:
                        suffix = " of Creepers";
                        break;
                    case 3:
                        suffix = ", Lined with Bedrock";
                        break;
                    case 4:
                        suffix = " of Dragon's Endurance";
                        break;
                    default:
                        break;
                }
                break;
            case "protection_fall":
                switch(rndNum) {
                    case 0:
                        suffix = " of Featherfall";
                        break;
                    case 1:
                        suffix = " of the Light-footed";
                        break;
                    case 2:
                        suffix = " of Airy Steps";
                        break;
                    case 3:
                        suffix = " of Minor Levitation";
                        break;
                    case 4:
                        suffix = " of the Breeze";
                        break;
                    default:
                        break;
                }
                break;
            case "protection_fire":
                switch(rndNum) {
                    case 0:
                        suffix = " of Fire Protection";
                        break;
                    case 1:
                        suffix = " of the Frost";
                        break;
                    case 2:
                        suffix = " of Blaze Resistance";
                        break;
                    case 3:
                        suffix = " of Firefighting";
                        break;
                    case 4:
                        suffix = " of Water's Gift";
                        break;
                    default:
                        break;
                }
                break;
            case "protection_projectile":
                switch(rndNum) {
                    case 0:
                        suffix = " of Arrow Protection";
                        break;
                    case 1:
                        suffix = " of the Wind";
                        break;
                    case 2:
                        suffix = " of Deflection";
                        break;
                    case 3:
                        suffix = " of Ranged Defense";
                        break;
                    case 4:
                        suffix = " of Cover";
                        break;
                    default:
                        break;
                }
                break;
            case "thorns":
                switch(rndNum) {
                    case 0:
                        suffix = " of Thorns";
                        break;
                    case 1:
                        suffix = " of the Porcupine";
                        break;
                    case 2:
                        suffix = " of Feedback";
                        break;
                    case 3:
                        suffix = " of Counterstriking";
                        break;
                    case 4:
                        suffix = " of Revenge";
                        break;
                    default:
                        break;
                }
                break;
            case "water_worker":
                switch(rndNum) {
                    case 0:
                        suffix = " of Undersea Mining";
                        break;
                    case 1:
                        suffix = " of the Merfolk";
                        break;
                    case 2:
                        suffix = " of Fluid Swings";
                        break;
                    case 3:
                        suffix = " of the Watershaper";
                        break;
                    case 4:
                        suffix = " of the Unhindered";
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }

        return suffix;
    }

    private String nameItem(Material m) {
        String[] parts = m.name().toLowerCase().split("_");

        if (parts.length > 1)
            return (generateItemPrefix(parts[0]) + " " + generateItemName(parts[1]));
        else
            return (generateItemName(parts[0]));
    }

    private Material getRandomMaterial(int num) {
        int rdm;
        Material m = null;

        switch(num){
            case 0:
                m = Material.BOW;
                rndIType = ItemType.WEAPON;
                break;
            case 1:
                m = Material.DIAMOND_AXE;
                rdm = r.nextInt(2);
                if (rdm == 0)
                    rndIType = ItemType.TOOL;
                else
                    rndIType = ItemType.WEAPON;
                break;
            case 2:
                m = Material.DIAMOND_BOOTS;
                rndIType = ItemType.ARMOR;
                break;
            case 3:
                m = Material.DIAMOND_CHESTPLATE;
                rndIType = ItemType.ARMOR;
                break;
            case 4:
                m = Material.DIAMOND_HELMET;
                rndIType = ItemType.ARMOR;
                break;
            case 5:
                m = Material.DIAMOND_HOE;
                rndIType = ItemType.TOOL;
                break;
            case 6:
                m = Material.DIAMOND_LEGGINGS;
                rndIType = ItemType.ARMOR;
                break;
            case 7:
                m = Material.DIAMOND_PICKAXE;
                rndIType = ItemType.TOOL;
                break;
            case 8:
                m = Material.DIAMOND_SPADE;
                rdm = r.nextInt(2);
                if (rdm == 0)
                    rndIType = ItemType.TOOL;
                else
                    rndIType = ItemType.WEAPON;
                break;
            case 9:
                m = Material.GOLD_AXE;
                rdm = r.nextInt(2);
                if (rdm == 0)
                    rndIType = ItemType.TOOL;
                else
                    rndIType = ItemType.WEAPON;
                break;
            case 10:
                m = Material.GOLD_BOOTS;
                rndIType = ItemType.ARMOR;
                break;
            case 11:
                m = Material.GOLD_CHESTPLATE;
                rndIType = ItemType.ARMOR;
                break;
            case 12:
                m = Material.GOLD_HELMET;
                rndIType = ItemType.ARMOR;
                break;
            case 13:
                m = Material.GOLD_HOE;
                rndIType = ItemType.TOOL;
                break;
            case 14:
                m = Material.GOLD_LEGGINGS;
                rndIType = ItemType.ARMOR;
                break;
            case 15:
                m = Material.GOLD_PICKAXE;
                rndIType = ItemType.TOOL;
                break;
            case 16:
                m = Material.GOLD_SPADE;
                rdm = r.nextInt(2);
                if (rdm == 0)
                    rndIType = ItemType.TOOL;
                else
                    rndIType = ItemType.WEAPON;
                break;
            case 17:
                m = Material.IRON_AXE;
                rdm = r.nextInt(2);
                if (rdm == 0)
                    rndIType = ItemType.TOOL;
                else
                    rndIType = ItemType.WEAPON;
                break;
            case 18:
                m = Material.IRON_BOOTS;
                rndIType = ItemType.ARMOR;
                break;
            case 19:
                m = Material.IRON_CHESTPLATE;
                rndIType = ItemType.ARMOR;
                break;
            case 20:
                m = Material.IRON_HELMET;
                rndIType = ItemType.ARMOR;
                break;
            case 21:
                m = Material.IRON_HOE;
                rndIType = ItemType.TOOL;
                break;
            case 22:
                m = Material.IRON_LEGGINGS;
                rndIType = ItemType.ARMOR;
                break;
            case 23:
                m = Material.IRON_PICKAXE;
                rndIType = ItemType.TOOL;
                break;
            case 24:
                m = Material.IRON_SPADE;
                rdm = r.nextInt(2);
                if (rdm == 0)
                    rndIType = ItemType.TOOL;
                else
                    rndIType = ItemType.WEAPON;
                break;
            case 25:
                m = Material.LEATHER_BOOTS;
                rndIType = ItemType.ARMOR;
                break;
            case 26:
                m = Material.LEATHER_CHESTPLATE;
                rndIType = ItemType.ARMOR;
                break;
            case 27:
                m = Material.LEATHER_HELMET;
                rndIType = ItemType.ARMOR;
                break;
            case 28:
                m = Material.LEATHER_LEGGINGS;
                rndIType = ItemType.ARMOR;
                break;
            case 29:
                m = Material.WOOD_AXE;
                rdm = r.nextInt(2);
                if (rdm == 0)
                    rndIType = ItemType.TOOL;
                else
                    rndIType = ItemType.WEAPON;
                break;
            case 30:
                m = Material.WOOD_HOE;
                rndIType = ItemType.TOOL;
                break;
            case 31:
                m = Material.WOOD_PICKAXE;
                rndIType = ItemType.TOOL;
                break;
            case 32:
                m = Material.WOOD_SPADE;
                rdm = r.nextInt(2);
                if (rdm == 0)
                    rndIType = ItemType.TOOL;
                else
                    rndIType = ItemType.WEAPON;
                break;
            default:
                m = Material.IRON_SWORD;
                rndIType = ItemType.WEAPON;
        }

        return m;
    }
}
