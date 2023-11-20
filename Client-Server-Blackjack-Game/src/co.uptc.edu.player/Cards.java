public class Cards {

    // Atributo para almacenar la cara de la carta
    private String Face;
    
    // Constructor de la clase que recibe el valor de la carta como parámetro
    public Cards(String cardVal){
        Face = cardVal;
    }
    
    // Método toString que devuelve la representación en cadena de la carta
    public String toString(){
        return Face;
    }
} 
