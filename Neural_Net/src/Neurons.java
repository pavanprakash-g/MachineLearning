import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by pavan on 3/1/17.
 */
public class Neurons {
    private int layerId;
    private String id;
    private float bias;
    private float value;
    private float del;
    private HashMap<String, Float> inputWeights = new HashMap<>();

    public float getDel() {
        return del;
    }

    public void setDel(float del) {
        this.del = del;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public HashMap<String, Float> getInputWeights() {
        return inputWeights;
    }

    public void setInputWeights(HashMap<String, Float> inputWeights) {
        this.inputWeights = inputWeights;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }


    public int getLayerId() {
        return layerId;
    }

    public void setLayerId(int layerId) {
        this.layerId = layerId;
    }

    public float getBias() {
        return bias;
    }

    public void setBias(float bias) {
        this.bias = bias;
    }


}
