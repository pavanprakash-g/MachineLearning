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
nbSum=0;
nbPrec=0;
for(i in 1:10) {
  testIndexes <- which(folds==i,arr.ind=TRUE)
  testd <- scaled[testIndexes, ]
  traind <- scaled[-testIndexes, ]
  
  #Naive Bayesian
  model <- naiveBayes(as.factor(traind$V11) ~ ., data = traind) 
  pred <- predict(model, testd)
  tab <- table(pred, testd$V11)    
  accuracy <- sum(diag(tab))/sum(tab)
  accuracy <-accuracy*100
  nbSum = nbSum+ accuracy
  conf <- table(pred = round(as.numeric(pred)), true = testd$V11)
  nbPrec = nbPrec+(diag(conf)*100/apply(conf,2,sum))
  #cat("Naive Bayesian Sample ", i,", accuracy= ", accuracy,"\n")
  
}

cat("Naive Bayesian total accuracy", nbSum/10,"\n")
cat("Naive Bayesian total Precision", mean(nbPrec)/10,"\n")

