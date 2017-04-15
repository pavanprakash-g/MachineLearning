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
nnSum=0;
nnPrec=0;
for(i in 1:10) {
  testIndexes <- which(folds==i,arr.ind=TRUE)
  testd <- scaled[testIndexes, ]
  traind <- scaled[-testIndexes, ]
  #neural net
  myformula <- as.formula(paste0('V11 ~ ', paste(names(traind[!names(traind) %in% 'V11']),collapse = ' + ')))
  nnmodel = neuralnet(myformula, data= traind,hidden = c(4,4,4),linear.output = F, threshold = 1)
  testSample <- subset(testd, select=columns)
  pred <- compute(nnmodel, testSample)
  accuracy <- sum(round(pred$net.result)==testd$V11)/length(testd$V11)
  accuracy <-accuracy*100
  nnSum = nnSum + accuracy
  conf <- table(pred = round(pred$net.result), true = testd$V11)
  nnPrec = nnPrec+(diag(conf)*100/apply(conf,2,sum))
  #cat("NeuralNet model Sample : ", i," accuracy= ", accuracy,"\n")
  #plot(nnmodel)
  
}

cat("NeuralNet total accuracy", nnSum/10,"\n")
cat("NeuralNet total Precision", mean(nnPrec)/10,"\n")

