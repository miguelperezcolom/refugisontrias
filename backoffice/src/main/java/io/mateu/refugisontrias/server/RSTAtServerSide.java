package io.mateu.refugisontrias.server;

import io.mateu.refugisontrias.model.EstadoUsuario;
import io.mateu.refugisontrias.model.Fichero;
import io.mateu.refugisontrias.model.Usuario;
import io.mateu.ui.core.server.BaseServerSideApp;
import io.mateu.ui.core.server.SQLTransaction;
import io.mateu.ui.core.server.ServerSideApp;
import io.mateu.ui.core.server.Utils;
import io.mateu.ui.core.shared.FileLocator;
import io.mateu.ui.core.shared.UserData;
import io.mateu.ui.mdd.server.util.Helper;
import io.mateu.ui.mdd.server.util.JPATransaction;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

/**
 * Created by miguel on 3/1/17.
 */
public class RSTAtServerSide extends BaseServerSideApp implements ServerSideApp {
    @Override
    public DataSource getJdbcDataSource() throws Exception {
        return null;
    }

    @Override
    public Object[][] select(String sql) throws Throwable {
        return Helper.select(sql);
    }

    @Override
    public void execute(String sql) throws Throwable {
        Helper.execute(sql);
    }

    @Override
    public Object selectSingleValue(String sql) throws Throwable {
        return Helper.selectSingleValue(sql);
    }

    @Override
    public void update(String sql) throws Throwable {
        Helper.update(sql);
    }

    @Override
    public int getNumberOfRows(String sql) {
       return Helper.getNumberOfRows(sql);
    }

    @Override
    public Object[][] selectPage(String sql, int desdeFila, int numeroFilas) throws Throwable {
        return Helper.selectPage(sql, desdeFila, numeroFilas);
    }

    @Override
    public void transact(SQLTransaction t) throws Throwable {

        Helper.transact(t);

    }

    @Override
    public void notransact(SQLTransaction t) throws Throwable {
        Helper.notransact(t);
    }

    @Override
    public UserData authenticate(String login, String password) throws Throwable {
        if (login == null || "".equals(login.trim()) || password == null || "".equals(password.trim())) throw new Exception("Username and password are required");
        UserData d = new UserData();
        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Exception {
                Usuario u = em.find(Usuario.class, login.toLowerCase().trim());
                if (u != null) {
                    if (u.getPassword() == null) throw new Exception("Missing password for user " + login);
                    if (!password.trim().equalsIgnoreCase(u.getPassword().trim())) throw new Exception("Wrong password");
                    if (EstadoUsuario.INACTIVO.equals(u.getEstado())) throw new Exception("Deactivated user");
                    d.setName(u.getNombre());
                    d.setEmail(u.getEmail());
                    d.setLogin(login);
                    if (u.isAdministrador()) {
                        d.getPermissions().add(1);
                    }
                } else throw new Exception("No user with login " + login);
            }
        });
        return d;
    }

    @Override
    public void forgotPassword(String login) throws Throwable {

    }

    @Override
    public void changePassword(String login, String oldPassword, String newPassword) throws Throwable {
        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Exception {
                Usuario u = em.find(Usuario.class, login.toLowerCase().trim());
                if (u != null) {
                    if (!oldPassword.trim().equalsIgnoreCase(u.getPassword().trim())) throw new Exception("Wrong old password");
                    u.setPassword(newPassword);
                } else throw new Exception("No user with login " + login);
            }
        });
    }

    @Override
    public void updateProfile(String login, String name, String email, FileLocator foto) throws Throwable {
        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Exception {
                Usuario u = em.find(Usuario.class, login.toLowerCase().trim());
                if (u != null) {
                    u.setNombre(name);
                    u.setEmail(email);
                } else throw new Exception("No user with login " + login);
            }
        });
    }

    @Override
    public UserData signUp(String s, String s1, String s2, String s3) throws Throwable {
        return null;
    }

    @Override
    public String recoverPassword(String s) throws Throwable {
        return null;
    }

    @Override
    public void updateFoto(String login, FileLocator fileLocator) throws Throwable {
        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Exception {
                Usuario u = em.find(Usuario.class, login.toLowerCase().trim());
                if (u != null) {
                    Fichero p = u.getFoto();
                    if (p == null) {
                        u.setFoto(p = new Fichero());
                        em.persist(p);
                    }
                    p.setName(fileLocator.getFileName());
                    p.setPath("");
                    p.setBytes(Utils.readBytes(fileLocator.getTmpPath()));
                } else throw new Exception("No user with login " + login);
            }
        });
    }
}
