trainingData = read.table(url("http://archive.ics.uci.edu/ml/machine-learning-databases/breast-cancer-wisconsin/breast-cancer-wisconsin.data"), header = FALSE, sep=",")
a <- trainingData == '?'
is.na(trainingData) <- a
trainingData <- na.omit(trainingData)
#removing factors
indx <- sapply(trainingData, is.factor)
trainingData[indx] <- lapply(trainingData[indx], function(x) as.numeric(as.character(x)))

maxs=apply(trainingData,MARGIN = 2,max)
mins=apply(trainingData,MARGIN = 2,min)
scaled <- as.data.frame(scale(trainingData, center = mins, scale = maxs - mins))
columns <- c("V1","V2","V3","V4","V5","V6","V7","V8","V11","V10")
#CrossValidation:
folds <- cut(seq(1,nrow(scaled)),breaks=10,labels=FALSE)
pSum=0;
pPrec=0;
sigmoid = function(x) {
  1 / (1 + exp(-x))
}
for(i in 1:10) {
  testIndexes <- which(folds==i,arr.ind=TRUE)
  testd <- scaled[testIndexes, ]
  traind <- scaled[-testIndexes, ]
  #Perceptron
  myformula <- as.formula(paste0('V11 ~ ', paste(names(traind[!names(traind) %in% 'V11']),collapse = ' + ')))
  pmodel = neuralnet(myformula, data= traind,hidden = 0,linear.output = F, threshold = 10, act.fct = sigmoid,err.fct="ce")
  #pmodel = neuralnet(myformula, data= traind,hidden = 0,linear.output = F, threshold = 0.1)
  testSample <- subset(testd, select=columns)
  pred <- compute(pmodel, testSample)
  accuracy <- sum(round(pred$net.result)==testd$V11)/length(testd$V11)
  accuracy <-accuracy*100
  pSum = pSum + accuracy
  conf <- table(pred = round(pred$net.result), true = testd$V11)
  pPrec = pPrec+(diag(conf)*100/apply(conf,2,sum))
  #cat("Perceptron model Sample : ", i," accuracy= ", accuracy,"\n")
  #plot(pmodel)
  
  
  
}

cat("Perceptron total accuracy", pSum/10,"\n")
cat("Perceptron total Precision", mean(pPrec)/10,"\n")

