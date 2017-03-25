import java.util.HashMap;

/**
 * Created by pavan on 3/1/17.
 */
public class DataInstance {
    HashMap<Integer, String> details = new HashMap<Integer, String>();

    DataInstance(String singleRow){
        String[] data = singleRow.split(",");
        int index = 0;
        for(int i=0; i< data.length; i++) {
            index++;
            if(!"?".equals(data[i])) {
                details.put(index, data[i]);
            }else{
                details.clear();
                break;
            }
        }
    }

}
