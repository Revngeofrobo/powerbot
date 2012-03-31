package org.powerbot.game.api.methods;

import java.awt.Canvas;
import java.awt.Point;

import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.widget.WidgetComposite;
import org.powerbot.game.api.wrappers.LocalTile;
import org.powerbot.game.api.wrappers.Tile;
import org.powerbot.game.api.wrappers.interactive.Player;
import org.powerbot.game.api.wrappers.widget.WidgetChild;
import org.powerbot.game.bot.Bot;
import org.powerbot.game.client.Client;
import org.powerbot.game.client.RSGroundBytes_Bytes;
import org.powerbot.game.client.RSGroundInfoTileData;
import org.powerbot.game.client.RSInfoGroundBytes;
import org.powerbot.game.client.RSInfoRSGroundInfo;
import org.powerbot.game.client.TileData;

/**
 * A utility for the manipulation of different calculations for the game.
 *
 * @author Timer
 */
public class Calculations {
	/**
	 * A representation of the game's (Java) Toolkit.
	 *
	 * @author Timer
	 */
	public static class Toolkit {
		public float absoluteX, absoluteY;
		public float xMultiplier, yMultiplier;
	}

	/**
	 * A representation of the game's Viewport, or Matrix.
	 *
	 * @author Timer
	 */
	public static class Viewport {
		public float xOff, xX, xY, xZ;
		public float yOff, yX, yY, yZ;
		public float zOff, zX, zY, zZ;
	}

	public static final int[] SIN_TABLE = new int[0x4000];
	public static final int[] COS_TABLE = new int[0x4000];

	static {
		final double d = 0.00038349519697141029D;
		for (int i = 0; i < 16384; i++) {
			Calculations.SIN_TABLE[i] = (int) (32768D * Math.sin(i * d));
			Calculations.COS_TABLE[i] = (int) (32768D * Math.cos(i * d));
		}
	}

	/**
	 * @param x     The local x position of the tile of which you desire to get the height for.
	 * @param y     The local y position of the tile of which you desire to get the height for.
	 * @param plane The plane to access this tile's information on.
	 * @return The height of the given tile on the provided plane.
	 */
	public static int calculateTileHeight(final int x, final int y, int plane) {
		final Client client = Bot.resolve().getClient();
		final int x1 = x >> 9;
		final int y1 = y >> 9;
		final byte[][][] settings = (byte[][][]) ((RSGroundBytes_Bytes) (((RSInfoGroundBytes) client.getRSGroundInfo()).getRSInfoGroundBytes())).getRSGroundBytes_Bytes();
		if (settings != null && x1 >= 0 && x1 < 104 && y1 >= 0 && y1 < 104) {
			if (plane <= 3 && (settings[1][x1][y1] & 2) != 0) {
				++plane;
			}
			final Object rsInfoGroundInfo = ((RSInfoRSGroundInfo) client.getRSGroundInfo()).getRSInfoRSGroundInfo();
			if (rsInfoGroundInfo != null) {
				final TileData[] planes = (TileData[]) ((RSGroundInfoTileData) rsInfoGroundInfo).getRSGroundInfoTileData();
				if (planes != null && plane < planes.length && planes[plane] != null) {
					final int[][] heights = planes[plane].getHeights();
					if (heights != null) {
						final int x2 = x & 0x200 - 1;
						final int y2 = y & 0x200 - 1;
						final int start_h = heights[x1][y1] * (0x200 - x2) + heights[x1 + 1][y1] * x2 >> 9;
						final int end_h = heights[x1][1 + y1] * (0x200 - x2) + heights[x1 + 1][y1 + 1] * x2 >> 9;
						return start_h * (512 - y2) + end_h * y2 >> 9;
					}
				}
			}
		}
		return 0;
	}

	/**
	 * @param x      The absolute x ground position.
	 * @param y      The absolute x ground position.
	 * @param plane  The plane to calculation this tile's position on.
	 * @param height The height offset.
	 * @return The <code>Point</code> of the given tile on the screen.
	 */
	public static Point groundToScreen(final int x, final int y, final int plane, final int height) {
		if (x < 512 || y < 512 || x > 52224 || y > 52224) {
			return new Point(-1, -1);
		}
		final int z = calculateTileHeight(x, y, plane) - height;
		return worldToScreen(x, z, y);
	}

	/**
	 * @param x Absolute x position of the calculation.
	 * @param y Depth of the requested calculation.
	 * @param z Absolute y position of the calculation.
	 * @return The <code>Point</code> of the given coordinates on screen.
	 */
	public static Point worldToScreen(final int x, final int y, final int z) {
		final Bot bot = Bot.resolve();
		final Toolkit toolkit = bot.toolkit;
		final Viewport viewport = bot.viewport;
		final float _z = (viewport.zOff + (viewport.zX * x + viewport.zY * y + viewport.zZ * z));
		final float _x = (viewport.xOff + (viewport.xX * x + viewport.xY * y + viewport.xZ * z));
		final float _y = (viewport.yOff + (viewport.yX * x + viewport.yY * y + viewport.yZ * z));
		if (_x >= -_z && _x <= _z && _y >= -_z && _y <= _z) {
			return new Point(
					Math.round(toolkit.absoluteX + (toolkit.xMultiplier * _x) / _z),
					Math.round(toolkit.absoluteY + (toolkit.yMultiplier * _y) / _z)
			);
		}
		return new Point(-1, -1);
	}

	/**
	 * Returns the <code>Point</code> of given x and y values in the game's 2D plane.
	 *
	 * @param x x value based on the game plane.
	 * @param y y value based on the game plane.
	 * @return <code>Point</code> within map; otherwise <tt>new Point(-1, -1)</tt>.
	 */
	public static Point worldToMap(double x, double y) {
		final Bot bot = Bot.resolve();
		final Client client = bot.getClient();
		final Player local = Players.getLocal();
		if (distance(local.getPosition(), new Tile((int) x, (int) y, 0)) > 17) {
			return new Point(-1, -1);
		}
		x -= Game.getBaseX();
		y -= Game.getBaseY();

		final LocalTile localTile = local.getLocalPosition();
		final int calculatedX = (int) (x * 4 + 2) - localTile.getX() / 0x80;
		final int calculatedY = (int) (y * 4 + 2) - localTile.getY() / 0x80;

		final WidgetChild mm2 = WidgetComposite.getMap();
		if (mm2 == null) {
			return new Point(-1, -1);
		}
		final int actDistSq = calculatedX * calculatedX + calculatedY * calculatedY;
		final int mmDist = 10 + Math.max(mm2.getWidth() / 2, mm2.getHeight() / 2);

		if (mmDist * mmDist >= actDistSq) {
			int angle = 0x3fff & (int) client.getMinimapAngle();
			final boolean setting4 = client.getMinimapSetting() * bot.multipliers.GLOBAL_MINIMAPSETTING == 4;

			if (!setting4) {
				angle = 0x3fff & (client.getMinimapOffset() * bot.multipliers.GLOBAL_MINIMAPOFFSET) + (int) client.getMinimapAngle();
			}

			int cs = Calculations.SIN_TABLE[angle];
			int cc = Calculations.COS_TABLE[angle];

			if (!setting4) {
				final int fact = 0x100 + (client.getMinimapScale() * bot.multipliers.GLOBAL_MINIMAPSCALE);
				cs = 0x100 * cs / fact;
				cc = 0x100 * cc / fact;
			}

			final int calcCenterX = cc * calculatedX + cs * calculatedY >> 0xf;
			final int calcCenterY = cc * calculatedY - cs * calculatedX >> 0xf;
			final int screen_x = calcCenterX + mm2.getAbsoluteX() + mm2.getWidth() / 2;
			final int screen_y = -calcCenterY + mm2.getAbsoluteY() + mm2.getHeight() / 2;
			return new Point(screen_x, screen_y);
		}

		return new Point(-1, -1);
	}

	/**
	 * @param point The <code>Point</code> to determine if it's on screen or not.
	 * @return <tt>true</tt> if the point is on the screen; otherwise <tt>false</tt>.
	 */
	public static boolean isPointOnScreen(final Point point) {
		final Canvas canvas = Bot.resolve().getCanvas();//TODO
		return point.x > 0 && point.y > 0 && point.x < canvas.getWidth() && point.y < canvas.getHeight();
	}

	public static double distance(final LocalTile tile1, final LocalTile tile2) {
		return Math.sqrt((tile1.getX() - tile2.getX()) * (tile1.getX() - tile2.getX()) + (tile1.getY() - tile2.getY()) * (tile1.getY() - tile2.getY()));
	}

	public static double distance(final int x1, final int y1, final int x2, final int y2) {
		return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
	}
}
