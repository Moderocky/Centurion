/**
 * This is an alternative for the Minecraft `@e[limit=1,distance=..5,...]` selector.
 * It imitates some of the basic behaviour but ignores options (dy, dx, x_rotation) that aren't likely to be useful in plugin commands.
 * It replaces these with new, modular and configurable parts so that plugins can extend the selector with useful selection criteria.
 * <p>
 * An example would be a region plugin adding a `region` criterion like `@a[region=my_region_id]`.
 */
package mx.kenzie.centurion.selector;
