//Intent
Intent camera = new Intent( MediaStore.ACTION_IMAGE_CAPTURE) ;
super.startActivityForResult(camera, REQ_CAMERA);


@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	try{
		switch(requestCode) {
			case REQ_CAMERA:
				Bitmap bitmap = (Bitmap) data.getExtras().get("data");
				break;
			default:
				break;
		}
	}catch(NullPointerException e){
		e.printStackTrace();
	}
	super.onActivityResult(requestCode, resultCode, data);
}