function toHttps(oldData) {
	if (!oldData) {
		return oldData;
	}
	if (oldData.indexOf("http://api.c-f.com") != -1) {
		return oldData.replace("http://api.c-f.com", "https://api.c-f.com");
	} else if (oldData.indexOf("http://s.files.c-f.com") != -1) {
		return oldData.replace("http://s.files.c-f.com",
				"https://resources.c-f.com");
	} else if (oldData.indexOf("http://pic2.l99.com") != -1) {
		return oldData.replace("http://pic2.l99.com", "https://pic2.l99.com");
	} else if (oldData.indexOf("http://www.c-f.com") != -1) {
		return oldData.replace("http://www.c-f.com", "https://www.c-f.com");
	} else if (oldData.indexOf("http://m.c-f.com") != -1) {
		return oldData.replace("http://m.c-f.com", "https://m.c-f.com");
	}
	return oldData;
}

function toNews(oldData) {
	if (!oldData) {
		return oldData;
	}
	var content = "https://api.c-f.com/football/wemedia/posts/";
	var index = oldData.indexOf(content);
	if (index == -1) {
		return oldData;
	}
	var index2 = oldData.indexOf("/html?key=visitor");
	var id = oldData.substring(content.length, index2);
	return "https://www.c-f.com/news/detail/" + id;
}

for (var bookmarks = db.bookmarks.find({}); bookmarks.hasNext();) {
	var bookmark = bookmarks.next();
	var id = bookmark._id;

	var newImage = toHttps(bookmark.image);
	var newUrl = toHttps(bookmark.url);
	newUrl = toNews(newUrl);
	var newShareUrl = toHttps(bookmark.shareUrl);

	printjson("newImage->" + newImage + "  newUrl->" + newUrl
			+ "  newShareUrl->" + newShareUrl);

	db.bookmarks.update({
		"_id" : bookmark._id
	}, {
		"$set" : {
			"image" : newImage,
			"url" : newUrl,
			"shareUrl" : newShareUrl
		}
	}, {
		"multi" : false,
		upsert : false
	});
}