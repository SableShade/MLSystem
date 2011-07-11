package me.Sable.MLmanager;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.entity.Player;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

public class DBManager {

	public MLManager plugin;

	public DBManager(MLManager instance) {
		this.plugin = instance;
	}

	public Connection createConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println(e);
		}
		try {
			return (Connection) DriverManager.getConnection("jdbc:mysql://"+ plugin.MySqlDir + "/"+plugin.database, plugin.MySqlUser  ,  plugin.MySqlPass );
		} catch (SQLException e) {
			System.out.println("[MLManager] Creating Connection Error: " + e);
		}
		return null;
	}

	public void prepare() {

		Connection conn = null;
		Statement stmt = null;
		
		try{
			conn = createConnection();
			
			stmt = (Statement) conn.createStatement();
			
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS `mls_players` ( `player_id` int(10) NOT NULL AUTO_INCREMENT, `player_name` text NOT NULL, `xp` int(10) unsigned NOT NULL, `level` int(10) NOT NULL, `spare_points` int(10) NOT NULL, PRIMARY KEY (`player_id`) ) ENGINE=MyISAM;");
			
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS `mls_stats` ( `stat_id` int(10) NOT NULL AUTO_INCREMENT, `stat_name` text NOT NULL, PRIMARY KEY (`stat_id`) ) ENGINE=MyISAM AUTO_INCREMENT=1 ;");
			
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS `mls_player_stats` ( `player_id` int(10) NOT NULL, `stat_id` int(10) NOT NULL, `points` int(10) NOT NULL ) ENGINE=MyISAM ;");
		} catch (SQLException e) {
			System.out.println("[MLManager] Creating Tables Error: " + e);
			return;
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				System.out.println("[MLManager] " + e);
			}
		}

	}

	// Fetches player data from the DB.
	public PlayerAtt getPlayerData(Player p) {
		PlayerAtt playerAtt = null;
		String playerName = p.getName();

		Connection conn = null;
		Statement stmt = null;

		String query = "SELECT player_id, xp, spare_points FROM mls_players WHERE player_name=('"
				+ playerName + "')";
		try {
			conn = createConnection();
			stmt = (Statement) conn.createStatement();

			ResultSet rs = stmt.executeQuery(query);

			while (rs.next()) {

				int pid = rs.getInt("player_id");
				int xpTotal = rs.getInt("xp");
				int spare = rs.getInt("spare_points");
				;

				playerAtt = new PlayerAtt(p, pid, xpTotal, spare, plugin);
			}

			int playerID = playerAtt.getPlayerID();
			query = "SELECT stat_id, points FROM mls_player_stats WHERE player_id=('"
					+ playerID + "')";

			rs = stmt.executeQuery(query);

			while (rs.next()) {

				int statValue = rs.getInt("stat_id");

				if (statValue == 1) {
					playerAtt.setStrength(rs.getInt("points"));
				} else if (statValue == 2) {
					playerAtt.setVitality(rs.getInt("points"));
				} else if (statValue == 3) {
					playerAtt.setDexterity(rs.getInt("points"));
				} else if (statValue == 4) {
					playerAtt.setAgility(rs.getInt("points"));
				} else if (statValue == 5) {
					playerAtt.setSpirit(rs.getInt("points"));
				}
			}
		} catch (SQLException e) {
			System.out.println("[MLManager] Fetching Player Data Error: " + e);
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				System.out.println("[MLManager] " + e);
			}
		}

		return playerAtt;
	}

	// Commits the PlayerAtt data to the DB.
	public void commitPlayerData(PlayerAtt p) {

		Connection conn = null;
		Statement st = null;
				
		try {

			conn = createConnection();

			st = (Statement) conn.createStatement();
			st.executeUpdate("UPDATE  `mls_players` SET  `xp` =  '"+p.getXP()+"', `level` =  '"+p.getLevel()+"',	`spare_points` =  '"+p.getSpare()+"' WHERE `player_id` ='"+p.getPlayerID()+"';");
			
			st.executeUpdate("UPDATE `mls_player_stats` SET  `points` =  '"+p.getStrength()+"' WHERE `player_id` = '"+p.getPlayerID()+"' AND `stat_id` = 1;");
			st.executeUpdate("UPDATE `mls_player_stats` SET  `points` =  '"+p.getVitality()+"' WHERE `player_id` = '"+p.getPlayerID()+"' AND `stat_id` = 2;");
			st.executeUpdate("UPDATE `mls_player_stats` SET  `points` =  '"+p.getDexterity()+"' WHERE `player_id` = '"+p.getPlayerID()+"' AND `stat_id` = 3;");
			st.executeUpdate("UPDATE `mls_player_stats` SET  `points` =  '"+p.getAgility()+"' WHERE `player_id` = '"+p.getPlayerID()+"' AND `stat_id` = 4;");
			st.executeUpdate("UPDATE `mls_player_stats` SET  `points` =  '"+p.getSpirit()+"' WHERE `player_id` = '"+p.getPlayerID()+"' AND `stat_id` = 5;");
			
		} catch (SQLException e) {
			System.out.println("[MLManager] Saving Player Data Error: " + e);
		} finally {
			try {
				if (st != null) {
					st.close();
				}
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				System.out.println("[MLManager] " + e);
			}
		}
		return;
	}

	// Checks if a player is in the DB.
	public boolean contains(Player p) {
		String playerName = p.getName();
		Connection conn = null;
		Statement st = null;
		boolean isTrue = false;
		try {

			conn = createConnection();

			st = (Statement) conn.createStatement();
			ResultSet rs = st
					.executeQuery("SELECT player_name FROM mls_players WHERE player_name=('"
							+ playerName + "')");
			while (rs.next()) {
				isTrue = true;
			}
		} catch (SQLException e) {
			System.out.println("[MLManager] Checking Player Data Error: " + e);
		} finally {
			try {
				if (st != null) {
					st.close();
				}
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				System.out.println("[MLManager] " + e);
			}
		}
		return isTrue;

	}

	// Creates the data for a new player in the DB.
	public void newP(Player player) {

		String name = player.getName();
		System.out.println(name);

		Connection conn = null;
		Statement st = null;
		try {
			conn = createConnection();
			st = (Statement) conn.createStatement();
			st.executeUpdate("INSERT INTO `mls_players` (`player_id`, `player_name`, `xp`, `level`, `spare_points`) VALUES (NULL, '"
					+ name + "', '0', '0', '0');");
			
			ResultSet rs = st
					.executeQuery("SELECT player_id FROM mls_players WHERE player_name=('"
							+ name + "')");

			int pid = 0;
			boolean found = false;

			while (rs.next()) {
				pid = rs.getInt("player_id");
				found = true;
			}
			if (found) {
				st.executeUpdate("INSERT INTO `mls_player_stats` (`player_id`, `stat_id`, `points`) VALUES ('"
						+ pid
						+ "', '1', '20'), ('"
						+ pid
						+ "', '2', '20'), ('"
						+ pid
						+ "', '3', '20'), ('"
						+ pid
						+ "', '4', '20'), ('"
						+ pid + "', '5', '20');");
			}

			player.sendMessage("Level and Stats created. Welcome!");

		} catch (SQLException e) {
			System.out.println("[MLManager] Creating New Player Data Error: " + e);
			return;
		} finally {
			try {
				if (st != null) {
					st.close();
				}
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				System.out.println("[MLManager] " + e);
			}
		}
	}
}
