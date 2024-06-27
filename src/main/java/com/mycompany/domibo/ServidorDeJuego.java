/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.domibo;

import com.mycompany.utilities.*;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

/**
 *
 * @author Pc
 */
public class ServidorDeJuego implements IObservadorDeServidorSocket {
    ServidorSocket servidorSocket;
    ManejadorDeConexionesBD manejadorDeConexionesBD;
    List<Usuario> usuarios=new ArrayList<>();
    List<Sala> salas=new ArrayList<>();
    
    public ServidorDeJuego(){
        servidorSocket=new ServidorSocket(12345);
        servidorSocket.agregarObservador(this);
        manejadorDeConexionesBD=new ManejadorDeConexionesBD();
    }
    @Override
    public void paqueteRecibido(String mensaje,String tokenDelCliente) {
        Paquete paquete=Paquete.deserializar(mensaje);
        String protocolo = paquete.obtenerProtocolo();
        String parametros=paquete.obtenerParametros();
        switch (protocolo) {
            case "LOGIN": {
                JSONObject parametrosJson = new JSONObject(parametros);
                String nombreDeUsuario = parametrosJson.getString("nombreDeUsuario");
                String contrasena = parametrosJson.getString("contrasena");
                ResultSet respuestaDeVerificacion=manejadorDeConexionesBD.verificarInicioDeSesion(nombreDeUsuario, contrasena);
                if(respuestaDeVerificacion!=null){
                    try {
                        int id=respuestaDeVerificacion.getInt("id");
                        String nombre=respuestaDeVerificacion.getString("name");
                        String correoElectronico=respuestaDeVerificacion.getString("email");
                        String telefono=respuestaDeVerificacion.getString("telefono");
                        boolean encontrado=false;
                        for (Usuario user : usuarios) {
                            if (user.getId()!=null && user.getId().equals(id)) {
                                user.addSocketToken(tokenDelCliente);
                                encontrado=true;
                                break;
                            }
                        }
                        if(!encontrado){
                            Usuario usuario = new Usuario(id,nombre,nombreDeUsuario,correoElectronico, telefono,contrasena);
                            usuario.addSocketToken(tokenDelCliente);
                            this.usuarios.add(usuario);
                        }
                        JSONObject parametrosDeRespuesta = new JSONObject();
                        parametrosDeRespuesta.put("estado", "true");
                        parametrosDeRespuesta.put("id", id);
                        parametrosDeRespuesta.put("nombre", nombre);
                        parametrosDeRespuesta.put("nombreDeUsuario", nombreDeUsuario);
                        parametrosDeRespuesta.put("correoElectronico", correoElectronico);
                        parametrosDeRespuesta.put("telefono",telefono);
                        parametrosDeRespuesta.put("contrasena", contrasena);
                        String parametrosString=parametrosDeRespuesta.toString();
                        String protocoloDeRespuesta="LOGIN";
                        Paquete paqueteDeRespuesta=new Paquete(protocoloDeRespuesta,parametrosString);
                        String paqueteSerializado=Paquete.serializar(paqueteDeRespuesta);
                        servidorSocket.enviarMensajeACliente(tokenDelCliente, paqueteSerializado);
                        System.out.println(paqueteSerializado);
                    } catch (SQLException ex) {
                        Logger.getLogger(ServidorDeJuego.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                break;
            }
            case "MOVER_FICHA":{
                System.out.println("Llega hasta mover ficha servidor");
                JSONObject parametrosJson = new JSONObject(parametros);
                String ficha = parametrosJson.getString("ficha");
                String posicion = parametrosJson.getString("posicion");
                JSONObject movimientoJson = new JSONObject();
                movimientoJson.put("ficha", ficha);
                movimientoJson.put("posicion", posicion);
                String parametrosStringMover=movimientoJson.toString();
                String protocoloDeRespuestaMover="MOVER_FICHA";
                Paquete paqueteDeRespuestaMover=new Paquete(protocoloDeRespuestaMover,parametrosStringMover);
                String paqueteSerializadoMover=Paquete.serializar(paqueteDeRespuestaMover);
                servidorSocket.enviarMensajeAClientes(tokenDelCliente, paqueteSerializadoMover);
                break;
            }
            case "REGISTRO": {
                JSONObject parametrosJson = new JSONObject(parametros);
                String nombre = parametrosJson.getString("nombre");
                String nombreDeUsuario = parametrosJson.getString("nombreDeUsuario");
                String correoElectronico = parametrosJson.getString("correoElectronico");
                String telefono = parametrosJson.getString("telefono");
                String contrasena = parametrosJson.getString("contrasena");
                Integer respuestaDeRegistro=manejadorDeConexionesBD.registrarJugadorEnLaBD(nombre,nombreDeUsuario,correoElectronico, telefono,contrasena );
                if(respuestaDeRegistro>0){
                    Usuario usuario = new Usuario(respuestaDeRegistro,nombre,nombreDeUsuario,correoElectronico, telefono,contrasena);
                    usuario.addSocketToken(tokenDelCliente);
                    this.usuarios.add(usuario);
                    
                    JSONObject parametrosDeRespuesta = new JSONObject();
                    parametrosDeRespuesta.put("estado", "true");
                    parametrosDeRespuesta.put("id", respuestaDeRegistro);
                    parametrosDeRespuesta.put("nombre", nombre);
                    parametrosDeRespuesta.put("nombreDeUsuario", nombreDeUsuario);
                    parametrosDeRespuesta.put("correoElectronico", correoElectronico);
                    parametrosDeRespuesta.put("telefono",telefono);
                    parametrosDeRespuesta.put("contrasena", contrasena);
                    
                    String parametrosString=parametrosDeRespuesta.toString();
                    String protocoloDeRespuesta="REGISTRO";
                    Paquete paqueteDeRespuesta=new Paquete(protocoloDeRespuesta,parametrosString);
                    String paqueteSerializado=Paquete.serializar(paqueteDeRespuesta);
                    servidorSocket.enviarMensajeACliente(tokenDelCliente, paqueteSerializado);
                    System.out.println(paqueteSerializado);
                }
              break; 
            } 
            case "CREAR": {
                JSONObject parametrosJson = new JSONObject(parametros);
                Integer idDeUsuario = parametrosJson.getInt("idUsuario");
                String nombreSala = parametrosJson.getString("nombre");
                String tokenSala = createSoketToken(idDeUsuario, nombreSala);
                
                Sala sala=new Sala(tokenSala, nombreSala);
                for(Usuario usuario:this.usuarios){
                    if(Objects.equals(usuario.getId(), idDeUsuario)){
                        sala.setCreador(usuario);
                        sala.addJugador(usuario);
                    }
                }
                this.salas.add(sala);
                
                JSONObject parametrosDeRespuesta = new JSONObject();
                parametrosDeRespuesta.put("estado", "true");
                parametrosDeRespuesta.put("token", tokenSala);
                parametrosDeRespuesta.put("nombre", nombreSala);

                String parametrosString=parametrosDeRespuesta.toString();
                String protocoloDeRespuesta="CREAR";
                Paquete paqueteDeRespuesta=new Paquete(protocoloDeRespuesta,parametrosString);
                String paqueteSerializado=Paquete.serializar(paqueteDeRespuesta);
                servidorSocket.enviarMensajeACliente(tokenDelCliente, paqueteSerializado);
                System.out.println(paqueteSerializado);
              break; 
            } 
            case "SALAS": {
                JSONObject parametrosDeRespuesta = new JSONObject();
                parametrosDeRespuesta.put("estado", "true");
                for(int i=0; i<salas.size();i++){
                    Sala sala = this.salas.get(i);
                    JSONObject parametrosDeSala = new JSONObject();
                    parametrosDeSala.put("token", sala.getToken());
                    parametrosDeSala.put("nombreSala", sala.getName());
                    parametrosDeSala.put("nombreCreador", sala.getCreador().getUsername());
                    String parametrosSalaString=parametrosDeSala.toString();

                    parametrosDeRespuesta.put("sala"+(i+1), parametrosSalaString);
                }
                String parametrosString=parametrosDeRespuesta.toString();
                String protocoloDeRespuesta="SALAS";
                Paquete paqueteDeRespuesta=new Paquete(protocoloDeRespuesta,parametrosString);
                String paqueteSerializado=Paquete.serializar(paqueteDeRespuesta);
                servidorSocket.enviarMensajeACliente(tokenDelCliente, paqueteSerializado);
                System.out.println(paqueteSerializado);
                break;
            }
            
            case "ENTRAR": {
                JSONObject parametrosJson = new JSONObject(parametros);
                String tokenSala = parametrosJson.getString("tokenSala");
                for(Sala sala: salas){
                    if(sala.getToken().equals(tokenSala)){
                        JSONObject parametrosDeRespuesta = new JSONObject();
                        parametrosDeRespuesta.put("estado", "true");
                        parametrosDeRespuesta.put("token", tokenSala);
                        parametrosDeRespuesta.put("nombre", sala.getName());
                        parametrosDeRespuesta.put("idCreador", sala.getCreador().getId());
                        parametrosDeRespuesta.put("nombreCreador", sala.getCreador().getUsername());
                        Usuario usuario =  new Usuario();
                        usuario.addSocketToken(tokenDelCliente);
                        sala.addJugador(usuario);
                        String parametrosString=parametrosDeRespuesta.toString();
                        String protocoloDeRespuesta="ENTRAR";
                        Paquete paqueteDeRespuesta=new Paquete(protocoloDeRespuesta,parametrosString);
                        String paqueteSerializado=Paquete.serializar(paqueteDeRespuesta);
                        servidorSocket.enviarMensajeACliente(tokenDelCliente, paqueteSerializado);
                        break;
                    }
                }
                
              break; 
            } 
            case "INICIAR": {
                JSONObject parametrosJson = new JSONObject(parametros);
                String tokenSala = parametrosJson.getString("tokenSala");
                for(Sala sala: salas){
                    if(sala.getToken().equals(tokenSala)){
                        List<Usuario> jugadores=sala.getJugadores();
                        for(Usuario usuario : jugadores){
                            String tokenJugador=usuario.getLastSocketToken();
                            JSONObject parametrosDeRespuesta = new JSONObject();
                            parametrosDeRespuesta.put("estado", "true");
                            String parametrosString=parametrosDeRespuesta.toString();
                            String protocoloDeRespuesta="INICIAR";
                            Paquete paqueteDeRespuesta=new Paquete(protocoloDeRespuesta,parametrosString);
                            String paqueteSerializado=Paquete.serializar(paqueteDeRespuesta);
                            servidorSocket.enviarMensajeACliente(tokenJugador, paqueteSerializado);
                        }
                        break;
                    }
                }
                break;
            }
            default:
                //
                break;
        }
    }
    
    public static String createSoketToken(int number, String text) {
        String combined = number + ":" + text;
        byte[] bytes = combined.getBytes(StandardCharsets.UTF_8);
        return Base64.getEncoder().encodeToString(bytes);
    }
    
    public static void main(String[] args) {
        ServidorDeJuego servidorDeJuego = new ServidorDeJuego();
    }

}
