@Override
public void onClick(View v) {
		switch (v.getId()) {
		case R.id.startSpeek: //start_btn
			Intent i=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
			startActivityForResult(i, REQ_SPEECH);
			break;
		default:
			break;
		}
}

@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	try{
		switch(requestCode) {
			case REQ_SPEECH:
				ArrayList<String> list=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

				if (list!=null) {
					String result = list.get(0).toString();
					// do sth
				}
				break;
			default:
				break;
		}
	}catch(NullPointerException e){
		e.printStackTrace();
	}
	super.onActivityResult(requestCode, resultCode, data);
}