import java.sql.SQLException;
import javafx.fxml.FXML;

import java.sql.ResultSet;
import java.sql.Statement;

import conect.Conexion;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class ControllClientes {
    String estadoCliente;
    int codigoCliente;
    Conexion con = new Conexion();
    

    @FXML private TextField txtApellido;
    
    @FXML private TextField txtEntrada;

    @FXML private TextField txtIdentifi;

    @FXML private TextField txtName;

    @FXML private Button btnDelete;

    @FXML private Button btnNew;

    @FXML private Button btnUpdate;

    @FXML private ImageView buscarImg;

    @FXML private ComboBox<String> cmbGenero;

    String query;

    @FXML
    void buscarImgClick(MouseEvent event) throws SQLException {
        //1. Recuperar el dato que se excriba en txtIdentifi
        //2. Validar que se escribio algo
        //3. consultar en la base de datos por un registro con esa identificacion
            //==> Se encuentra
                    //Desplegar los datos
                    //Activar los botones Eliminar y Actualizar
                    //Desactivar el boton nuevo
            //==> No se encuentra
                    //notifico
        String iden = txtIdentifi.getText();
        if (iden==null || iden.isEmpty()) {
            txtEntrada.setText("Digite una identificación valida para poder buscar");
        }
        else{
            con.conectar();
            try(Statement stm = con.getCon().createStatement()){
                int ident = Integer.parseInt(txtIdentifi.getText());
                ResultSet rta = stm.executeQuery("select * from clientes where cedula = '"+ident+"'");
                if(rta.next()){
                    codigoCliente = rta.getInt("idClientes");
                    estadoCliente = rta.getString("estado");
                    txtName.setText(rta.getString("nombre"));
                    txtApellido.setText(rta.getString("apellidos"));
                    cmbGenero.setValue(rta.getString("genero"));
                    if (estadoCliente.equalsIgnoreCase("A")) {
                        btnDelete.setText("Borrar");
                    } else {
                        btnDelete.setText("Recuperar");
                    }
                    btnDelete.setDisable(false);
                    btnUpdate.setDisable(false);
                    txtEntrada.clear();
                }
                else
                    txtEntrada.setText("No se encontro registro que coincida con la identificación");
            }
            con.desconectar();
        }

    }

    @FXML
    void actionUpdate(MouseEvent event) throws SQLException {
        //Recuperar los valores suministrados
        //Preparar la consulta a utilizar (Actualizar todo los campos)
        //Ejecuta la consulta
        
        String ide = txtIdentifi.getText();
        String nom = txtName.getText();
        String ape = txtApellido.getText();
        String gen = cmbGenero.getValue();
        
        if(ide==null || ide.isEmpty())
            txtEntrada.setText("Debe ingresar una Identificacion valida");
        else if(nom == null || nom.isEmpty())
            txtEntrada.setText("Debe ingresar un Nombre valido");
        else if(ape == null || ape.isEmpty())
            txtEntrada.setText("Debe ingresar un Apellido valido");
        else if(gen == null || gen.isEmpty())
            txtEntrada.setText("Debe ingresar un genero valido");
        else{
            String query1 = "UPDATE clientes set cedula = '"+ide+"', nombre = '"+nom+"' , apellidos = '"+ape+"', genero = '"+gen+"' WHERE cedula = "+ide;
            con.conectar();
            System.out.println("voy bien");
            try(Statement stm = con.getCon().createStatement()){
            //Ejecuta comando de accion
            int res = stm.executeUpdate(query1);
            if(res!=0){
                txtEntrada.setText("Registro Actualizado con exito");
            }else
                txtEntrada.setText("Error al Actualizar registro");
            restaurarDatos();
            } 
            con.desconectar();
        }

    }

    @FXML
    void actionDelete(MouseEvent event) throws SQLException {
        //Recuperar el texto del boton
        //Preparar la consulta a utilizar (Borrar --> estado=I, recuperar--> estado =A)
        //Ejecuta la consulta
        String acc = btnDelete.getText();

        if ("Borrar".equalsIgnoreCase(acc)) {
            query = "UPDATE clientes set estado = 'I' where idClientes="+codigoCliente;
        } else {
            query = "UPDATE clientes set estado = 'A' where idClientes="+codigoCliente;
        }
        con.conectar();
        try(Statement stm = con.getCon().createStatement()){
        //Ejecuta comando de accion
        int res = stm.executeUpdate(query);
        if(res!=0){
            txtEntrada.setText("Registro Recuperado/Borrado con exito");
        }else
            txtEntrada.setText("Error al recuperar/Borrar registro");
        restaurarDatos();
        }
        con.desconectar();
    }
    
    private void restaurarDatos(){
        
        txtName.clear();
        txtApellido.clear();
        txtIdentifi.clear();
        cmbGenero.setValue("M");
        btnDelete.setDisable(true);
        btnDelete.setText("Borrar");
        btnUpdate.setDisable(true);
    }


    @FXML
    void actionNew(MouseEvent event) throws SQLException {
        //Recuperar datos del formulario
        //Validar Datos
        //Preparar la insercion
        String ide = txtIdentifi.getText();
        String nom = txtName.getText();
        String ape = txtApellido.getText();
        String gen = cmbGenero.getValue();
        if(ide==null || ide.isEmpty())
            txtEntrada.setText("Debe ingresar una Identificacion valida");
        else if(nom==null || nom.isEmpty())
            txtEntrada.setText("Debe ingresar un Nombre valido");
        else if(ape==null || ape.isEmpty())
            txtEntrada.setText("Debe ingresar un Apellido valido");
        else if(gen==null || gen.isEmpty())
            txtEntrada.setText("Debe ingresar un genero valido");
        else{
            String query1 = "insert into clientes (cedula,nombre,apellidos,genero)values ('"+ide+"','"+nom+"','"+ape+"','"+gen+"')";
            con.conectar();
            try (Statement stm = con.getCon().createStatement()){
                int rest = stm.executeUpdate(query1);
                if(rest != 0){
                    txtEntrada.setText("Datos Registrados con exito");
                    restaurarDatos();
                }
                else{
                    txtEntrada.setText("Error al grabar los datos por favor verifique");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            con.desconectar();
        }

    }


    
    @FXML
    void initialize(){ // Metodo de javafx que sirve para inicializar combox y demas en java apenas se abra una ventana
        btnDelete.setDisable(true);
        btnUpdate.setDisable(true);
        // Para inicializar valores de combo de genero
        cmbGenero.getItems().clear();
        cmbGenero.getItems().addAll("M", "F");
        cmbGenero.setValue("Select your gender");
    }
}
