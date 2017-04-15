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
lrSum=0;
lrPrec=0;
for(i in 1:10) {
  testIndexes <- which(folds==i,arr.ind=TRUE)
  testd <- scaled[testIndexes, ]
  traind <- scaled[-testIndexes, ]
  
  #Logistic Regression
  lrmodel <- glm(V11~.,data=traind,family=binomial(link="logit"), maxit=10, trace=T)
  pred = predict(lrmodel, type="response", newdata=testd)
  accuracy <- sum(testd$V11==round(pred))/length(testd$V11)
  accuracy <- accuracy*100
  lrSum = lrSum + accuracy
  conf <- table(pred = round(as.numeric(pred)), true = testd$V11)
  lrPrec = lrPrec+(diag(conf)*100/apply(conf,2,sum))
  #cat("Logistic Regression Sample ", i," accuracy= ", accuracy,"\n")
  
}

cat("Logistic Regression total accuracy", lrSum/10,"\n")
cat("Logistic Regression total Precision", mean(lrPrec)/10,"\n")

