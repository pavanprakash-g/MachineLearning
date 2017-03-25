import java.io.*;
import java.util.*;

/**
 * Created by pavan on 3/1/17.
 */
public class NeuralNet {
    private static ArrayList<Integer> indexes;
    private static float outputValue;
    private static HashMap<Integer, Float> sumLabelWise;
    private static HashMap<Integer, Layers> map = new HashMap<>();
    private static int instanceCount;
    private static final float lr = (float)0.3;
    private static float allowedError;
    private static float finalError;

    public static void getData(ArrayList<DataInstance> data, String fileName) throws Exception{
        indexes = new ArrayList<>();
        String line="";
        int count = 0;
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        while ((line = br.readLine()) != null) {
            line = line.trim().replaceAll(","," ").replaceAll(" +", ",");
            if(count==0){
                String[] values = line.split(",");
                for(int j=1; j<=values.length;j++) {
                    indexes.add(j);
                }
                count++;
            }
            DataInstance i = new DataInstance(line);
            if(i.details.size()>1) {
                data.add(i);
            }
            instanceCount++;
        }
        br.close();
    }


    private static ArrayList<Neurons> getInputNeurons(int attributeCount){
        int id = 0;
        ArrayList<Neurons> neurons = new ArrayList<>();
        for(int i=0;i<attributeCount; i++){
            id++;
            Neurons neurons1 = new Neurons();
            neurons1.setLayerId(1);
            neurons1.setId(""+id);
            neurons1.setValue(indexes.get(i));
            neurons.add(neurons1);
        }
        return neurons;
    }

    private static float randValue(){
        float min = (float) -4.0;
        float max = (float) 4.0;
        final Random random = new Random();
        return  random.nextFloat()* (max - min) + min ;
        //return 1;
    }

    private static HashMap<String,Float> getRandomWeights(int previousLayerId){
        HashMap<String,Float> weights = new HashMap<>();
        int weightCount = map.get(previousLayerId).getNumOfNodes();
        for(int i=1; i<=weightCount; i++){
            weights.put(""+i, randValue());
        }
        return weights;
    }

    private static Neurons designHiddenLayer(int layerId, int id){
        int previousLayerId = layerId-1;
        Neurons neurons = new Neurons();
        neurons.setInputWeights(getRandomWeights(previousLayerId));
        neurons.setLayerId(layerId);
        neurons.setId(""+id);
        neurons.setBias(randValue());
        return neurons;
    }

    private static float sigmoid(float value){
        float sig = 1/(1+ (float)Math.exp(-value));
        return sig;
    }

    private static float findValue(Neurons neurons, ArrayList<Neurons> previousNeurons){
        float sum = (float) 0.0;
        for(HashMap.Entry<String, Float> w : neurons.getInputWeights().entrySet()){
            for(Neurons singleNeuron : previousNeurons){
                if(singleNeuron.getId().equals(w.getKey())) {
                    sum += (singleNeuron.getValue() * w.getValue()) ;
                }

            }
        }
        sum += neurons.getBias();
        return sigmoid(sum);
    }

    public static String getStackTrace(final Throwable throwable) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }

    private static boolean forwardPass(DataInstance instance){
        float value;
        for(HashMap.Entry<Integer, Layers> layer: map.entrySet()){
            Layers l = layer.getValue();
            if(!l.isInput){
                for(Neurons n : l.getNeurons()){
                    value = findValue(n, map.get(l.getLayerId()-1).getNeurons());
                    n.setValue(value);
                    if(l.isOutput){
                        outputValue = value;
                    }
                }
            }
        }
        return errorTolerable(instance);
    }

    private static boolean errorTolerable(DataInstance instance){
        float sum = (float)0.0;
        float targetOutput;
        float error;
        targetOutput = Float.parseFloat(instance.details.get(instance.details.size()));
        sum += Math.pow(targetOutput-outputValue,2);
        error = (float)0.5*sum;
        finalError = error;
        if(error > allowedError){
            return false;
        }else{
            return true;
        }
    }

    private static void instantiateInput(DataInstance instance, Layers input){
        ArrayList<Neurons> inputs = new ArrayList<>(input.getNeurons());
        for(int i=1; i<= instance.details.size(); i++){
            for(Neurons input1 : inputs){
                if(Integer.parseInt(input1.getId()) == i) {
                    input1.setValue(Float.parseFloat(instance.details.get(i)));
                }
            }
        }
    }

    private static float findDel(boolean isOutput, String t1, Neurons neurons){
        float x = neurons.getValue();
        float t = Float.parseFloat(t1);
        float del;
        float currentWeight;
        float updatedWeight;
        if(isOutput){
            del = x*(1-x)*(t-x);
        }else{
            float sum = (float) 0.0;
            Layers l = map.get(neurons.getLayerId()+1);
            for(Neurons n : l.getNeurons()){
                sum += n.getInputWeights().get(neurons.getId())*n.getDel();
                currentWeight = n.getInputWeights().get(neurons.getId());
                updatedWeight = currentWeight + lr*n.getDel()*neurons.getValue();
                n.getInputWeights().replace(neurons.getId(), updatedWeight);
            }
            del = x*(1-x)* sum;
        }
        return del;
    }

    private static void backwardPass(DataInstance singleInstance){
        ArrayList<Integer> keys = new ArrayList<>(map.keySet());
        for(int i=keys.size()-1; i>=0;i--){
            Layers l = map.get(keys.get(i));
            for(Neurons n : l.getNeurons()){
                n.setDel(findDel(l.isOutput, singleInstance.details.get(singleInstance.details.size()),n));
                if(l.isOutput || l.isHidden) {
                    n.setBias(n.getBias() + lr * n.getDel());
                }
            }
        }
    }

    private static boolean errorTolerable(ArrayList<DataInstance> dataInstances){
        float sum = (float)0.0;
        float targetOutput;
        float error;
        DataInstance instance = dataInstances.get(dataInstances.size()-1);
        targetOutput = Float.parseFloat(instance.details.get(instance.details.size()));
        sum += Math.pow(targetOutput-outputValue,2);
        error = (float)0.5*sum;
        finalError = error;
        if(error > allowedError){
            return false;
        }else{
            return true;
        }
    }

    private static void backPropagation(ArrayList<DataInstance> dataInstances){
        boolean isStop = false;
        int count = 0;
        int i=0;
        boolean isAcceptable = false;
        while(!isStop) {
            if(count < 1000) {
                for (DataInstance singleInstance : dataInstances) {
                    instantiateInput(singleInstance, map.get(1));
                    isAcceptable = forwardPass(singleInstance);
                    if(i>0 && isAcceptable){
                        i=1;
                        break;
                    }
                    backwardPass(singleInstance);
                }
                i = i+1;
                if(isAcceptable){
                    isStop = true;
                }
                /*if (errorTolerable(dataInstances)) {
                    isStop = true;
                }*/
                count++;
            }else{
                break;
            }
        }
        //System.out.println(count);
    }

    private static void testing(ArrayList<DataInstance> dataInstances){
        for (DataInstance singleInstance : dataInstances) {
            instantiateInput(singleInstance, map.get(1));
            forwardPass(singleInstance);
        }
        errorTolerable(dataInstances);
        System.out.println("Total test Error::"+finalError);
    }

    private static void printNet(){
        int count = 0;
        ArrayList<Integer> keys = new ArrayList<>(map.keySet());
        ArrayList<Neurons> list;
        for(int i=0; i<keys.size(); i++){
            if(i == 0){
                continue;
            }
            if(i != keys.size()-1) {
                list = map.get(i+1).getNeurons();
                System.out.println("Hidden Layer " + (i));
            }else{
                list = map.get(i+1).getNeurons();
                System.out.println("OutPut Layer 1");
            }
            for(Neurons n : list){
                count++;
                System.out.println("Neuron " + count + " Weights:"+ n.getBias() + " "+n.getInputWeights().toString());

            }
            count=0;
        }
    }

    public static void main(String[] args){
        ArrayList<Layers> hiddenLayers = new ArrayList<>();
        ArrayList<DataInstance> data = new ArrayList<>();
        ArrayList<DataInstance> trainingData = new ArrayList<>();
        ArrayList<DataInstance> testData = new ArrayList<>();
        int layerId = 0;
        try {
            //Taking input
            getData(data, args[0]);
            PreProcessing.preprocessData(data, args[0], indexes, instanceCount);
            float percentage = Float.parseFloat(args[1])/100;
            for(int i= 1 ; i<= data.size()*percentage; i++){
                trainingData.add(data.get(i-1));
            }
            for(int i = trainingData.size(); i<data.size(); i++){
                testData.add(data.get(i));
            }
            allowedError = Float.parseFloat(args[2]);
            int noOfHiddenLayers = Integer.parseInt(args[3]);
            layerId++;
            //Input layer
            Layers input = new Layers(true,false,false, layerId);
            input.setNeurons(getInputNeurons(indexes.size()-1));
            input.setNumOfNodes(input.getNeurons().size());
            map.put(layerId,input);
            int neuronId = 0;
            int noOfNeurons = 0;
            //Hidden layers
            for(int i=0; i<noOfHiddenLayers; i++){
                neuronId = 0;
                layerId++;
                Layers hidden = new Layers(false,true,false,layerId);
                map.put(layerId, hidden);
                noOfNeurons = Integer.parseInt(args[i+4]);
                ArrayList<Neurons> neuronsList = new ArrayList<>();
                for(int j=0; j<noOfNeurons; j++) {
                    neuronId++;
                    neuronsList.add(designHiddenLayer(layerId,neuronId));
                }
                hidden.setNeurons(neuronsList);
                hidden.setNumOfNodes(hidden.getNeurons().size());
                hiddenLayers.add(hidden);
            }

            //output Layer
            layerId++;
            Layers output = new Layers(false, false, true, layerId);
            Neurons outputNode = new Neurons();
            outputNode.setId(""+1);
            outputNode.setLayerId(layerId);
            outputNode.setBias(randValue());
            outputNode.setInputWeights(getRandomWeights(output.getLayerId()-1));
            ArrayList<Neurons> out = new ArrayList<>();
            out.add(outputNode);
            output.setNeurons(out);
            output.setNumOfNodes(output.getNeurons().size());
            map.put(layerId,output);
            backPropagation(trainingData);
            printNet();
            System.out.println("Total Training Error::"+finalError+"\n");
            System.out.println("Testing");
            System.out.println("-----------------------------------------------");
            testing(testData);
        }catch (Exception e){
            System.out.println(getStackTrace(e));
        }
    }
}
