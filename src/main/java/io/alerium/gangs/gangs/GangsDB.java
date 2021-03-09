package io.alerium.gangs.gangs;

import io.alerium.gangs.GangsPlugin;
import io.alerium.gangs.gangs.objects.Gang;
import lombok.RequiredArgsConstructor;

import java.sql.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

@RequiredArgsConstructor
public class GangsDB {
    
    private final GangsPlugin plugin;
    
    public List<Gang> loadGangs() {
        try (
                Connection connection = plugin.getMySQL().getConnection();
                PreparedStatement gangsStatement = connection.prepareStatement("SELECT * FROM gangs;");
                PreparedStatement gangMemberStatement = connection.prepareStatement("SELECT * FROM gangs_members;");

                ResultSet gangsResult = gangsStatement.executeQuery();
                ResultSet gangMemberResult = gangMemberStatement.executeQuery();
        ) {
            Map<Integer, List<UUID>> members = new HashMap<>();
            while (gangMemberResult.next()) {
                int gangId = gangMemberResult.getInt("gang_id");
                if (!members.containsKey(gangId))
                    members.put(gangId, new ArrayList<>());
                
                members.get(gangId).add(UUID.fromString(gangMemberResult.getString("uuid")));
            }
            
            List<Gang> gangs = new ArrayList<>();
            while (gangsResult.next()) {
                int id = gangsResult.getInt("id");
                gangs.add(new Gang(
                        id,
                        gangsResult.getString("name"),
                        UUID.fromString(gangsResult.getString("owner")),
                        members.containsKey(id) ? members.get(id) : new ArrayList<>(),
                        gangsResult.getBoolean("open")
                ));
            }
            
            return gangs;
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "An error occurred while loading gangs", e);
        }
        
        return null;
    }
    
    public void updateGangs(List<Gang> gangs) {
        try (
                Connection connection = plugin.getMySQL().getConnection();
                PreparedStatement statement = connection.prepareStatement("UPDATE gangs SET open = ? WHERE id = ?;");
        ) {
            for (Gang gang : gangs) {
                statement.setBoolean(1, gang.isOpen());
                statement.setInt(2, gang.getId());   
                statement.addBatch();
            }

            statement.executeBatch();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "An error occurred while saving gangs", e);
        }
    }
    
    public CompletableFuture<Integer> createGang(String name, UUID owner) {
        CompletableFuture<Integer> cf = new CompletableFuture<>();
        plugin.async(() -> {
            try (
                    Connection connection = plugin.getMySQL().getConnection();
                    PreparedStatement statement = connection.prepareStatement("INSERT INTO gangs (name, owner, open) VALUES (?, ?, ?);", Statement.RETURN_GENERATED_KEYS);
            ) {
                statement.setString(1, name);
                statement.setString(2, owner.toString());
                statement.setBoolean(3, false);
                
                statement.execute();
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (!generatedKeys.next()) {
                    cf.completeExceptionally(new SQLException("No key was generated."));
                    return;
                }
                
                cf.complete(generatedKeys.getInt("id"));
                generatedKeys.close();
            } catch (SQLException e) {
                cf.completeExceptionally(e);
            }
        });
        return cf;
    }
    
    public CompletableFuture<Void> deleteGang(int id) {
        CompletableFuture<Void> cf = new CompletableFuture<>();
        plugin.async(() -> {
            try (
                    Connection connection = plugin.getMySQL().getConnection();
                    PreparedStatement statement1 = connection.prepareStatement("DELETE FROM gangs WHERE id = ?;");
                    PreparedStatement statement2 = connection.prepareStatement("DELETE FROM gangs_members WHERE gang_id = ?;");
            ) {
                statement1.setInt(1, id);
                statement2.setInt(1, id);

                statement1.execute();
                statement2.execute();
                cf.complete(null);
            } catch (SQLException e) {
                cf.completeExceptionally(e);
            }
        });
        return cf;
    }
    
    public CompletableFuture<Void> addMember(Gang gang, UUID member) {
        CompletableFuture<Void> cf = new CompletableFuture<>();
        plugin.async(() -> {
            try (
                    Connection connection = plugin.getMySQL().getConnection();
                    PreparedStatement statement = connection.prepareStatement("INSERT INTO gangs_members (gang_id, uuid) VALUES (?, ?);");
            ) {
                statement.setInt(1, gang.getId());
                statement.setString(2, member.toString());
                
                statement.execute();
                cf.complete(null);
            } catch (SQLException e) {
                cf.completeExceptionally(e);
            }
        });
        return cf;
    }
    
    public CompletableFuture<Void> removeMember(Gang gang, UUID member) {
        CompletableFuture<Void> cf = new CompletableFuture<>();
        plugin.async(() -> {
            try (
                    Connection connection = plugin.getMySQL().getConnection();
                    PreparedStatement statement = connection.prepareStatement("DELETE FROM gangs_members WHERE gang_id = ? AND uuid = ?;");
            ) {
                statement.setInt(1, gang.getId());
                statement.setString(2, member.toString());

                statement.execute();
                cf.complete(null);
            } catch (SQLException e) {
                cf.completeExceptionally(e);
            }
        });
        return cf;
    }
    
}
