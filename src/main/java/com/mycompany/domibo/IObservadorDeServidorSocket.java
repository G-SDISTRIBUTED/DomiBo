/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.domibo;

/**
 *
 * @author Pc
 */
public interface IObservadorDeServidorSocket {
    void paqueteRecibido(String paquete, String tokenDelCliente);
}
