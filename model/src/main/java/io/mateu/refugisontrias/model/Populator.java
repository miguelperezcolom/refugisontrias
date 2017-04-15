package io.mateu.refugisontrias.model;

import io.mateu.ui.mdd.server.util.Helper;
import io.mateu.ui.mdd.server.util.JPATransaction;

/**
 * Created by miguel on 19/2/17.
 */
public class Populator {

    public static void main(String... args) throws Throwable {

        System.out.println("Populating database...");


        //authentication
        Helper.transact((JPATransaction) (em)->{

            // create user admin
            Usuario u = new Usuario();
            u.setLogin("admin");
            u.setNombre("Admin");
            u.setPassword(Helper.md5("1"));
            u.setEstado(EstadoUsuario.ACTIVO);
            em.persist(u);

        });

        // multilanguage


        System.out.println("Database populated.");

    }

}
