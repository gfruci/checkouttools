import java.util.List;

/**
 * @author Nandor_Sebestyen
 */
public class Main {

    public static void main(String[] args){

        Translator t =  new Translator();

        try {
            List<Output> outputs = t.urlCall();
            saveToExcel(outputs);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void saveToExcel(List<Output> outputs){
        for(Output output : outputs){
               System.out.println(output.toString());
        }
    }
}
