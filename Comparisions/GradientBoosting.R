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
gbmSum=0;
gbmPrec=0;
for(i in 1:10) {
  testIndexes <- which(folds==i,arr.ind=TRUE)
  testd <- scaled[testIndexes, ]
  traind <- scaled[-testIndexes, ]
  
  #Gradient Boosting
  gb = gbm.fit(traind[,1:(11-1)],traind[,11],n.trees=1,verbose = FALSE,shrinkage=0.5 ,bag.fraction = 0.3 ,interaction.depth = 2,n.minobsinnode = 1, distribution = "bernoulli")
  predicted <- predict(gb,testd[,1:(11-1)],n.trees=1)
  gbm_table <- table(testd[,11], predicted)
  accuracy <- (sum(diag(gbm_table)) / sum(gbm_table))*100.0
  gbmSum = gbmSum+accuracy
  conf <- table(pred = round(as.numeric(predicted)), true = testd$V11)
  gbmPrec = gbmPrec+(diag(conf)*100/apply(conf,2,sum))
  cat("Gradient Boosting Model Sample ", i," accuracy= ", accuracy,"\n")
  
}

cat("Gradient Boosting total accuracy", gbmSum/10,"\n")
cat("Gradient Boosting total Precision", mean(gbmPrec)/10,"\n")

