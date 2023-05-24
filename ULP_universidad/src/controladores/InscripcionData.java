
package controladores;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import modelo.Alumno;
import modelo.Inscripcion;
import modelo.Materia;

public class InscripcionData {
    private Connection con;
    private MateriaData matData;
    private AlumnoData aluData;

    public InscripcionData() {
        con = Conexion.getConexion();
    }
    
    public void guardarInscripcion (Inscripcion inscripcion){
        String sql = "INSERT INTO inscripcion(nota, id_alumno, id_materia) VALUES (?,?,?);";
        try {
            PreparedStatement ps = con.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            ps.setDouble(1,inscripcion.getNota()); 
            ps.setInt(2,inscripcion.getAlumno().getId());
            ps.setInt(3,inscripcion.getMateria().getId_materia());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                inscripcion.setId_inscripto(rs.getInt(1));
                System.out.println("Se ha inscripto correctamente el alumno");
            } else {
                System.out.println("No se ha inscripto al alumno");
            }
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(InscripcionData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public List<Inscripcion> obtenerInscripciones(){
        List<Inscripcion> listaInscriptos = new ArrayList<>();
        String sql = "SELECT * FROM inscripcion";
        PreparedStatement ps;
        try {
            ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                   Inscripcion i = new Inscripcion();
                   i.setId_inscripto(rs.getInt("id_inscripto"));
                   Alumno a = aluData.buscarAlumno(rs.getInt("id_alumno"));
                   i.setAlumno(a);
                   Materia m = matData.buscarMateria(rs.getInt("id_materia"));
                   i.setMateria(m);
                   i.setNota(rs.getDouble("nota"));
                   listaInscriptos.add(i);
        }
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(InscripcionData.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (listaInscriptos.isEmpty()) {
            System.out.println("La base de datos se encuentra vacia");
        }
        return listaInscriptos;
        
    }
    public List<Inscripcion> obtenerInscripcionXAlumno(int id_alumno) {
        List<Inscripcion> lista = new ArrayList<>();
        Inscripcion cursada;
        try {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM inscripcion WHERE id_alumno = ?", Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, id_alumno);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                cursada = new Inscripcion();
                cursada.setId_inscripto(rs.getInt("id_inscripto"));
                
                Alumno a=aluData.buscarAlumno(rs.getInt("id_alumno"));
                cursada.setAlumno(a);
                
                Materia m=matData.buscarMateria(rs.getInt("id_materia"));
                cursada.setMateria(m);
                cursada.setNota(rs.getInt("nota"));
                lista.add(cursada);
            }
            ps.close();
        } catch (SQLException ex) 
        {            
             System.out.println("Error al buscar al alumno con la id ingresada: ");            
        }
        if (lista.isEmpty()) 
        {
            System.out.println("La base de datos se encuentra vacia");      
        }
        return lista;
    }
    
    public List<Materia> obtenerMateriasCursadas(int id_alumno) {
        List<Materia> lista = new ArrayList<>();
        Materia m;
        try {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM inscripcion, materia WHERE incripcion.id_materia = materia.id_materia and inscripcion.id_alumno = ?");
                   
            ps.setInt(1, id_alumno);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                m = new Materia();
                m.setId_materia(rs.getInt("id_materia"));
                m.setNombre(rs.getString("nombre"));
                m.setAnio(rs.getInt("anio"));
                m.setEstado(rs.getBoolean("estado"));
                lista.add(m);
            }
            ps.close();
        } catch (SQLException ex) {
            
            System.out.println("Error al buscar al alumno con la id ingresada: " );
            
        }
        if (lista.isEmpty()) {
           System.out.println("La base de datos se encuentra vacia");
           
        }
        return lista;
    }
    public List<Materia> obtenerMateriasNOCursadas(int id_materia) {
        List<Materia> noCursada = new ArrayList<>();
        Materia m;
        try {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM materia WHERE id_materia NOT IN(SELECT materia.id_materia FROM materia, inscripcion WHERE materia.id_materia = inscripcion.id_materia AND inscripcion.id_alumno = ?);");
            ps.setInt(1, id_materia);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                m = new Materia();
                m.setId_materia(rs.getInt("id_materia"));
                m.setNombre(rs.getString("nombre_materia"));
                m.setAnio(rs.getInt("año"));
                m.setEstado(rs.getBoolean("estado"));
                noCursada.add(m);
            }
            ps.close();
        } catch (SQLException ex) {
            System.out.println("Error al consultar la tabla: " );
            
        }
        if (noCursada.isEmpty()) {
            
             System.out.println("La lista esta vacia." );
            
        }
        return noCursada;
    }
    
    public void borrarInscripcionMateriaAlumno(int id_alumno, int id_materia) {
        try {
            PreparedStatement ps = con.prepareStatement("DELETE FROM inscripcion WHERE id_alumno = ? AND id_materia = ?", Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, id_alumno);
            ps.setInt(2, id_materia);
            if (ps.executeUpdate() == 1) {
                System.out.println("La nota del alumno ha sido borrada correctamente" );
                
            } else {
                System.out.println("La nota del alumno no ha sido borrada." );
                
            }
            ps.close();
        } catch (SQLException ex) {
            System.out.println("Error al borrar la nota del alumno: ");
            
        }
    }
    
    public void actualizarNota(int id_alumno, int id_materia, double nota) {
        try {
            PreparedStatement ps = con.prepareStatement("UPDATE inscripcion SET nota = ? WHERE id_alumno = ? AND id_materia = ?", Statement.RETURN_GENERATED_KEYS);
            ps.setDouble(1, nota);
            ps.setInt(2, id_alumno);
            ps.setInt(3, id_materia);
            if (ps.executeUpdate() == 1) {
                System.out.println("La nota del alumno ha sido actualizada correctamente.");
                
            } else {
                System.out.println("La nota del alumno ha podido ser actualizada correctamente.");
                
            }
            ps.close();
        } catch (SQLException ex) {
            System.out.println("Error al actualizar la nota de un alumno: ");
          
        }
    }
    
}
