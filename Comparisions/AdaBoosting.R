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
adbSum=0;
adbPrec=0;
for(i in 1:10) {
  testIndexes <- which(folds==i,arr.ind=TRUE)
  testd <- scaled[testIndexes, ]
  traind <- scaled[-testIndexes, ]
  
  #Ada boosting
  model <- ada(traind$V11 ~ ., data = traind, iter=20, nu=1, type="discrete")
  p=predict(model,testd)
  accuracy <- sum(testd$V11==p)/length(p)
  accuracy <- accuracy * 100
  adbSum = adbSum+accuracy
  conf <- table(pred = round(as.numeric(p)), true = testd$V11)
  adbPrec = adbPrec+(diag(conf)*100/apply(conf,2,sum))
  #cat("Ada boosting Model Sample ", i," accuracy= ", accuracy,"\n")
  
}

cat("Ada boosting total accuracy", adbSum/10,"\n")
cat("Random Forest total Precision", mean(adbPrec)/10,"\n")

