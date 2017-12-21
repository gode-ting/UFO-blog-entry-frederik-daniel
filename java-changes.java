// Before out of memory 
@RequestMapping(method = RequestMethod.GET)
public int getLatest(){
	List<Post> maxObject = repository.findAll(new Sort(Sort.Direction.DESC, "hanesstID"));
	int latest = !maxObject.isEmpty() ?  maxObject.get(0).getHanesstID() : 0;
	return latest;
}

// After refactoring
@RequestMapping(method = RequestMethod.GET)
public int getLatest(){
	Query query = new Query();
	query.with(new Sort(Sort.Direction.DESC, "hanesstID"));
	query.limit(5);
	List<Post> maxObject = mongoTemplate.find(query, Post.class);
	int latest = !maxObject.isEmpty() ?  maxObject.get(0).getHanesstID() : 0;
	return latest;
}