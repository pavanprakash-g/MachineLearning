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
baggSum=0;
baggPrec=0;
for(i in 1:10) {
  testIndexes <- which(folds==i,arr.ind=TRUE)
  testd <- scaled[testIndexes, ]
  traind <- scaled[-testIndexes, ]
  
  #Bagging
  bgmodel <- bagging(V11~., data=traind, coob=F, nbBag=100)
  bgpred <- predict(bgmodel, testd)
  result <- data.frame(actual = testd$V11, prediction = bgpred)
  accuracy <- sum(round(result$prediction)==testd$V11)/length(testd$V11)
  accuracy <- accuracy*100
  baggSum = baggSum+ accuracy
  conf <- table(pred = round(as.numeric(bgpred)), true = testd$V11)
  baggPrec = knnPrec+(diag(conf)*100/apply(conf,2,sum))
  #cat("Bagging Sample ", i," accuracy= ", accuracy,"\n")
}

cat("Bagging accuracy", baggSum/10,"\n")
cat("Bagging Precision", mean(baggPrec)/10,"\n")

