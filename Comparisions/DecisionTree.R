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
dtSum=0;
dtPrec=0;
for(i in 1:10) {
  testIndexes <- which(folds==i,arr.ind=TRUE)
  testd <- scaled[testIndexes, ]
  traind <- scaled[-testIndexes, ]
  
  train_tree = rpart(V11~., data=traind, parms = list(split="information"), control = rpart.control(minsplit = 3,minbucket = 10, cp = 0.01, maxdepth = 30))
  testpred = predict(train_tree, newdata=testd)
  conf <- table(pred = round(testpred, digits=1), true = testd$V11)
  tab <- table(testpred, testd$V11)
  accuracy <- sum(testd$V11==round(testpred, digits = 1))/length(testpred)
  accuracy <- accuracy*100
  dtSum = dtSum+accuracy
  dtPrec = dtPrec+(diag(conf)*100/apply(conf,2,sum))
  #cat("D Tree Sample  = ", i,", accuracy= ", accuracy,"\n")    
  
}

cat("D Tree total accuracy", dtSum/10,"\n")
cat("D Tree total Precision", mean(dtPrec)/10,"\n")

