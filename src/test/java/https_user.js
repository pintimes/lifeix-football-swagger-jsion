function toHttps(oldData){
	if(!oldData){
		return oldData;
	}
	if(oldData.indexOf("http://s.files.c-f.com")!=-1){
		return oldData.replace("http://s.files.c-f.com","https://resources.c-f.com");
	}
	return oldData;
}

for(var users = db.users.find({});users.hasNext();){
	var user = users.next();
	var id = user._id ;

	var newImage = toHttps(user.avatar);
	
	printjson("newImage->"+newImage);
	
	db.users.update({"_id":user._id},{"$set":{"avatar":newImage}},{"multi":false,upsert:false})	;
}