/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.domibo;

import com.mycompany.utilities.Paquete;
import org.json.JSONObject;

/**
 *
 * @author Pc
 */
public class ServidorDeJuego implements IObservadorDeServidorSocket {
    ServidorSocket servidorSocket;
    ManejadorDeConexionesBD manejadorDeConexionesBD;
    
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
            case "LOGIN":
                Boolean respuestaDeVerificacion=manejadorDeConexionesBD.verificarInicioDeSesion(parametros);
                if(respuestaDeVerificacion){
                    JSONObject parametrosDeRespuesta = new JSONObject();
                    parametrosDeRespuesta.put("estado", "true");
                    String parametrosString=parametrosDeRespuesta.toString();
                    String protocoloDeRespuesta="LOGIN";
                    Paquete paqueteDeRespuesta=new Paquete(protocoloDeRespuesta,parametrosString);
                    String paqueteSerializado=Paquete.serializar(paqueteDeRespuesta);
                    servidorSocket.enviarMensajeACliente(tokenDelCliente, paqueteSerializado);
                }
                break;
            case "LOGOUT":
                //
                break;
            case "REGISTRO":
                Boolean respuestaDeRegistro=manejadorDeConexionesBD.registrarJugadorEnLaBD(parametros);
                if(respuestaDeRegistro){
                    JSONObject parametrosDeRespuesta = new JSONObject();
                    parametrosDeRespuesta.put("estado", "true");
                    String parametrosString=parametrosDeRespuesta.toString();
                    String protocoloDeRespuesta="REGISTRO";
                    Paquete paqueteDeRespuesta=new Paquete(protocoloDeRespuesta,parametrosString);
                    String paqueteSerializado=Paquete.serializar(paqueteDeRespuesta);
                    servidorSocket.enviarMensajeACliente(tokenDelCliente, paqueteSerializado);
                }
                break;
            default:
                //
                break;
        }
    }
    
    public static void main(String[] args) {
        ServidorDeJuego servidorDeJuego = new ServidorDeJuego();
    }
    
}
