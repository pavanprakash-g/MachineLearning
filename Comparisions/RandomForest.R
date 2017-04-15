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
rfAccsum=0;
rfPresum=0;
for(i in 1:10) {
  testIndexes <- which(folds==i,arr.ind=TRUE)
  testd <- scaled[testIndexes, ]
  traind <- scaled[-testIndexes, ]
  
  #Random Forest
  rfModel <- randomForest(as.factor(traind$V11)~., data=traind, importance=T, proximity=T, ntree=500)
  RFpred <- predict(rfModel,testd,type='response')
  predTable <- table(observed = testd$V11, predicted = RFpred)
  rfAccuracy <- sum(diag(predTable))/sum(predTable)
  rfAccuracy <-rfAccuracy*100
  rfAccsum=rfAccsum+rfAccuracy
  conf <- table(pred = round(as.numeric(RFpred)), true = testd$V11)
  rfPresum = rfPresum+(diag(conf)*100/apply(conf,2,sum))
  #cat("Random Forest Sample", i,", accuracy= ", rfAccuracy,"\n")
}

cat("Random Forest total accuracy", rfAccsum/10,"\n")
cat("Random Forest total Precision", mean(rfPresum)/10,"\n")

