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
svmSum=0;
svmPrec=0;
for(i in 1:10) {
  testIndexes <- which(folds==i,arr.ind=TRUE)
  testd <- scaled[testIndexes, ]
  traind <- scaled[-testIndexes, ]
  
  #SVM Model
  svmModel <- svm(V11~., data = traind, kernel="radial", cost = 1, gamma = 0.000015) 
  SVMprediction <- predict(svmModel, testd)
  conf <- table(pred = round(SVMprediction+0.05, digits=1), true = testd$V11)
  accuracy <-sum(diag(conf))/length(testd$V11)
  accuracy <-accuracy*100
  svmSum = svmSum+accuracy
  conf <- table(pred = round(SVMprediction), true = testd$V11)
  svmPrec = svmPrec+(diag(conf)*100/apply(conf,2,sum))
  #cat("SVM Model - Sample", i," accuracy= ", accuracy,"\n")
  
}

cat("SVM  total accuracy", svmSum/10,"\n")
cat("SVM total Precision", mean(svmPrec)/10,"\n")

