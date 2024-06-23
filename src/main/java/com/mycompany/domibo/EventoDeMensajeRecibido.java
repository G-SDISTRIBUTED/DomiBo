/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.domibo;

import java.util.EventObject;

/**
 *
 * @author Pc
 */
public class EventoDeMensajeRecibido extends EventObject {
    private String mensaje;
    private String tokenDeCliente;

    public EventoDeMensajeRecibido(Object fuente,String mensaje, String tokenDeCliente) {
        super(fuente);
        this.mensaje = mensaje;
        this.tokenDeCliente=tokenDeCliente;
    }
    
    public EventoDeMensajeRecibido(Object fuente,String mensaje) {
        super(fuente);
        this.mensaje = mensaje;
        this.tokenDeCliente=null;
    }
    
    public String obtenerMensaje(){
        return mensaje;
    }
    
    public String obtenerTokenDeCliente(){
        return tokenDeCliente;
    }
    
    void modicarTokenDeCliente(String token) {
        this.tokenDeCliente=token;
    }
}
