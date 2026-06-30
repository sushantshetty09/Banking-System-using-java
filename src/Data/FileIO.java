package Data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import Bank.*;

public class FileIO {
	
	public static Bank bank = null;
	public static boolean isLoading = false;
	private static final String DB_URL = "jdbc:sqlite:bank.db";

	private static Connection getConnection() throws Exception {
		Class.forName("org.sqlite.JDBC");
		return DriverManager.getConnection(DB_URL);
	}

	public static void initializeDatabase() {
		String sql = "CREATE TABLE IF NOT EXISTS accounts (" +
		             "  acc_num TEXT PRIMARY KEY," +
		             "  name TEXT NOT NULL," +
		             "  balance REAL NOT NULL," +
		             "  min_balance REAL NOT NULL," +
		             "  type TEXT NOT NULL," +
		             "  rate REAL," +
		             "  max_withdraw_limit REAL," +
		             "  institution_name TEXT," +
		             "  trade_license_number TEXT" +
		             ");";
		try (Connection conn = getConnection();
		     Statement stmt = conn.createStatement()) {
			stmt.execute(sql);
		} catch (Exception e) {
			System.err.println("Error initializing database: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static void Read() {
		initializeDatabase();
		FileIO.isLoading = true;
		try {
			FileIO.bank = new Bank();
			
			String sql = "SELECT * FROM accounts";
			try (Connection conn = getConnection();
			     Statement stmt = conn.createStatement();
			     ResultSet rs = stmt.executeQuery(sql)) {
				
				while (rs.next()) {
					String acc_num = rs.getString("acc_num");
					String name = rs.getString("name");
					double balance = rs.getDouble("balance");
					double min_balance = rs.getDouble("min_balance");
					String type = rs.getString("type");
					
					BankAccount acc = null;
					if ("Savings".equals(type)) {
						double maxw = rs.getDouble("max_withdraw_limit");
						acc = new SavingsAccount(name, 2000.0, maxw);
					} else if ("Student".equals(type)) {
						String inst = rs.getString("institution_name");
						acc = new StudentAccount(name, 20000.0, inst);
					} else if ("Current".equals(type)) {
						String tradeLic = rs.getString("trade_license_number");
						acc = new CurrentAccount(name, 5000.0, tradeLic);
					} else {
						acc = new BankAccount(name, min_balance, min_balance);
					}
					
					acc.setAccNum(acc_num);
					acc.setBalance(balance);
					
					FileIO.bank.addAccount(acc);
				}
			} catch (Exception e) {
				System.err.println("Error reading database: " + e.getMessage());
				e.printStackTrace();
			}
		} finally {
			FileIO.isLoading = false;
		}
	}

	public static void Write() {
		if (FileIO.bank == null) {
			return;
		}
		
		initializeDatabase();
		
		String deleteSql = "DELETE FROM accounts;";
		String insertSql = "INSERT INTO accounts (" +
		                   "  acc_num, name, balance, min_balance, type, rate, max_withdraw_limit, institution_name, trade_license_number" +
		                   ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
		                   
		try (Connection conn = getConnection()) {
			conn.setAutoCommit(false);
			try (Statement deleteStmt = conn.createStatement();
			     PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
				
				deleteStmt.executeUpdate(deleteSql);
				
				for (BankAccount acc : FileIO.bank.getAccounts()) {
					if (acc == null) {
						continue;
					}
					
					pstmt.setString(1, acc.getAccNum());
					pstmt.setString(2, acc.getName());
					pstmt.setDouble(3, acc.getbalance());
					pstmt.setDouble(4, acc.getMinBalance());
					
					if (acc instanceof StudentAccount) {
						StudentAccount sa = (StudentAccount) acc;
						pstmt.setString(5, "Student");
						pstmt.setDouble(6, 0.05); // rate
						pstmt.setDouble(7, 20000.0); // maxWithLimit
						pstmt.setString(8, sa.getInstitutionName());
						pstmt.setNull(9, java.sql.Types.VARCHAR);
					} else if (acc instanceof SavingsAccount) {
						SavingsAccount sa = (SavingsAccount) acc;
						pstmt.setString(5, "Savings");
						pstmt.setDouble(6, 0.05); // rate
						pstmt.setDouble(7, sa.getMaxWithLimit());
						pstmt.setNull(8, java.sql.Types.VARCHAR);
						pstmt.setNull(9, java.sql.Types.VARCHAR);
					} else if (acc instanceof CurrentAccount) {
						CurrentAccount ca = (CurrentAccount) acc;
						pstmt.setString(5, "Current");
						pstmt.setNull(6, java.sql.Types.DOUBLE);
						pstmt.setNull(7, java.sql.Types.DOUBLE);
						pstmt.setNull(8, java.sql.Types.VARCHAR);
						pstmt.setString(9, ca.getTradeLicenseNumber());
					} else {
						pstmt.setString(5, "Base");
						pstmt.setNull(6, java.sql.Types.DOUBLE);
						pstmt.setNull(7, java.sql.Types.DOUBLE);
						pstmt.setNull(8, java.sql.Types.VARCHAR);
						pstmt.setNull(9, java.sql.Types.VARCHAR);
					}
					
					pstmt.addBatch();
				}
				pstmt.executeBatch();
				conn.commit();
			} catch (Exception ex) {
				conn.rollback();
				throw ex;
			}
		} catch (Exception e) {
			System.err.println("Error writing database: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
