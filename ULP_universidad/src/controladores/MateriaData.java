
package controladores;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import modelo.Materia;

public class MateriaData {
    private Connection con;

    public MateriaData() {
        con = Conexion.getConexion();
    }
    
    public void guardarMateria(Materia materia){
        String sql =  "INSERT INTO materia(nombre,anio,estado)VALUES (?,?,?)";
        try {
            PreparedStatement ps = con.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            ps.setString(1,materia.getNombre());
           ps.setInt(2, materia.getAnio());
           ps.setBoolean(3, materia.isEstado());
           ps.executeUpdate();
           ResultSet rs= ps.getGeneratedKeys();
           if (rs.next()){
               materia.setId_materia(rs.getInt(1));
           } else {
               System.out.println("La materia no se pudo guardar");
           }
           ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(MateriaData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void actualizarMateria(Materia materia){
        String sql = "UPDATE materia SET nombre=?,anio=?,estado=? WHERE id_materia=?";
        try {
            PreparedStatement ps = con.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            ps.setString(1,materia.getNombre());
            ps.setInt(2, materia.getAnio());
            ps.setBoolean(3, materia.isEstado());
            ps.setInt(4, materia.getId_materia());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(MateriaData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public Materia buscarMateria(int id){
        Materia mat = null;
        String sql = "SELECT id_materia, nombre, anio, estado FROM materia WHERE id_materia=?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1,id);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                mat = new Materia();
                mat.setId_materia(rs.getInt("id_materia"));
                mat.setNombre(rs.getString("nombre"));
                mat.setAnio(rs.getInt("anio"));
                mat.setEstado(rs.getBoolean("estado"));
            } else {
                System.out.println("Materia inexistente");
            }
        } catch (SQLException ex) {
            Logger.getLogger(MateriaData.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return mat;
    }
    
    
}