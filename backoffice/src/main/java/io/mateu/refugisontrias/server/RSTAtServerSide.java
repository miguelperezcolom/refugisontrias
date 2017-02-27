package io.mateu.refugisontrias.server;

import io.mateu.refugisontrias.model.EstadoUsuario;
import io.mateu.refugisontrias.model.Usuario;
import io.mateu.ui.core.server.BaseServerSideApp;
import io.mateu.ui.core.server.SQLTransaction;
import io.mateu.ui.core.server.ServerSideApp;
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
    public Object[][] select(String sql) throws Exception {
        return Helper.select(sql);
    }

    @Override
    public void execute(String sql) throws Exception {
        Helper.execute(sql);
    }

    @Override
    public Object selectSingleValue(String sql) throws Exception {
        return Helper.selectSingleValue(sql);
    }

    @Override
    public void update(String sql) throws Exception {
        Helper.update(sql);
    }

    @Override
    public int getNumberOfRows(String sql) {
       return Helper.getNumberOfRows(sql);
    }

    @Override
    public Object[][] selectPage(String sql, int desdeFila, int numeroFilas) throws Exception {
        return Helper.selectPage(sql, desdeFila, numeroFilas);
    }

    @Override
    public void transact(SQLTransaction t) throws Exception {

        Helper.transact(t);

    }

    @Override
    public void notransact(SQLTransaction t) throws Exception {
        Helper.notransact(t);
    }

    @Override
    public UserData authenticate(String login, String password) throws Exception {
        UserData d = new UserData();
        Helper.transact(new JPATransaction() {
            @Override
            public void run(EntityManager em) throws Exception {
                Usuario u = em.find(Usuario.class, login.toLowerCase().trim());
                if (u != null) {
                    if (!password.trim().equalsIgnoreCase(u.getPassword().trim())) throw new Exception("Wrong password");
                    if (EstadoUsuario.INACTIVO.equals(u.getEstado())) throw new Exception("Deactivated user");
                    d.setName(u.getNombre());
                    d.setEmail(u.getEmail());
                    d.setLogin(login);
                } else throw new Exception("No user with login " + login);
            }
        });
        return d;
    }

    @Override
    public void changePassword(String login, String oldPassword, String newPassword) throws Exception {
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
    public void updateProfile(String login, String name, String email, FileLocator foto) throws Exception {
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
}
