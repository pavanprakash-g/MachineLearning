trainingData = read.table(url("http://archive.ics.uci.edu/ml/machine-learning-databases/breast-cancer-wisconsin/breast-cancer-wisconsin.data"), header = FALSE, sep=",")
a <- trainingData == '?'
is.na(trainingData) <- a
trainingData <- na.omit(trainingData)
#removing factors
indx <- sapply(trainingData, is.factor)
trainingData[indx] <- lapply(trainingData[indx], function(x) as.numeric(as.character(x)))

# maxs=apply(trainingData,MARGIN = 2,max)
# mins=apply(trainingData,MARGIN = 2,min)
#scaled <- as.data.frame(scale(trainingData, center = mins, scale = maxs - mins))
scaled <- trainingData
columns <- c("V1","V2","V3","V4","V5","V6","V7","V8","V11","V10")
#CrossValidation:
folds <- cut(seq(1,nrow(scaled)),breaks=10,labels=FALSE)
knnSum=0;
knnPrec=0;
for(i in 1:10) {
  testIndexes <- which(folds==i,arr.ind=TRUE)
  testd <- scaled[testIndexes, ]
  traind <- scaled[-testIndexes, ]
  
  #KNN Model
  pred <- knn(train=traind, test=testd,cl=traind$V11, k=300)
  accuracy = 100* sum(pred == testd$V11)/length(pred)
  knnSum = knnSum+ accuracy
  conf <- table(pred = round(as.numeric(pred)), true = testd$V11)
  knnPrec = knnPrec+(diag(conf)*100/apply(conf,2,sum))
  #cat("kNN Model Sample ",i , " accuracy= ", accuracy,"\n")
  
  
}

cat("KNN total accuracy", knnSum/10,"\n")
cat("KNN total Precision", mean(knnPrec)/10,"\n")

