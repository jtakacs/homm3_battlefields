#homm3_battlefields

#Download

[BattlefieldExplorer.jar](https://github.com/jtakacs/homm3_battlefields/blob/master/dist/BattlefieldExplorer.jar)
[Test map](https://github.com/jtakacs/homm3_battlefields/blob/master/resources/test_map/battlefield_explorer.h3m)

#changelog

* **remote control** - change battlefields from other programs, possibly from Unleashed Map Editor
* **28 swamp locations fixed** - accidentally, by making the code a bit more readable :)
* **Test map update** - Valeska can visit these 28 swamp locations
* **Area selection dialog** - you can restrict the searched area
* **Visual aid for area selection** - you can use your own map image
* **Pattern mirroring** - manually, or automatically during search
* **Pattern fixing** - for searching specific blocked hexes
* **War machine display** - visual aid mainly for fixed patterns
* **Anchor cell display** - if you wonder, why can't you put a force field on some cells.
* **HDmod plugin to hook into the real battlefield generator** - test was successful.
* **Keyboard event filter** - you can now enter wasd into text fields without side effects
* **BigIntegers were replaced with BitVector** - search is approximately 10x faster

#remote control

You can use HTTP POST to change the current battlefield. The software is listening on localhost:7777, and it accepts messages in the following form:

&lt;TERRAIN;XXX;YYY&gt;

where TERRAIN can be one of the following:
*  DIRT
*  SAND
*  GRASS
*  SNOW
*  SWAMP
*  ROUGH
*  UNDERGROUND
*  LAVA
*  SHIP
*  SHORE
*  MAGIC_PLAINS
*  CURSED_GROUND
*  HOLY_GROUND
*  EVIL_FOG
*  CLOVER_FIELDS
*  LUCID_POOLS
*  FIERY_FIELDS
*  ROCKLANDS
*  MAGIC_CLOUDS

and XXX and YYY can be any integer between 0 and 143.

#todo

* non standard large map support
* HotA new terrains support
