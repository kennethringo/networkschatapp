public class Image implements Serializable{
	private String userFrom = null;
    private String userTo=null;
    private byte[] image = null;
    private byte[] size;

    public Image(String userFrom, String userTo, byte[] image , byte [] size){
    	this.userFrom = userFrom;
    	this.userTo = userTo;
    	this.image = image;
    	this.size = size;
    }
}//end of class