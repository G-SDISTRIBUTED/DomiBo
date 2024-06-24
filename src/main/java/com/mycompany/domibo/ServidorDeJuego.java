/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.domibo;

import com.mycompany.utilities.Paquete;

/**
 *
 * @author Pc
 */
public class ServidorDeJuego implements IObservadorDeServidorSocket {
    ServidorSocket servidorSocket;
    
    public ServidorDeJuego(){
        servidorSocket=new ServidorSocket(12345);
        servidorSocket.agregarObservador(this);
    }
    @Override
    public void paqueteRecibido(String mensaje,String tokenDelCliente) {
        Paquete paquete=Paquete.deserializar(mensaje);
        String protocolo = paquete.obtenerProtocolo();
        switch (protocolo) {
            case "LOGIN":
                //
                break;
            case "LOGOUT":
                //
                break;
            case "REGISTER":
                //
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
