package detectfacesdb;

import java.util.List;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.AgeRange;
import com.amazonaws.services.rekognition.model.AmazonRekognitionException;
import com.amazonaws.services.rekognition.model.Attribute;
import com.amazonaws.services.rekognition.model.DetectFacesRequest;
import com.amazonaws.services.rekognition.model.DetectFacesResult;
import com.amazonaws.services.rekognition.model.Emotion;
import com.amazonaws.services.rekognition.model.Eyeglasses;
import com.amazonaws.services.rekognition.model.FaceDetail;
import com.amazonaws.services.rekognition.model.Gender;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.S3Object;
import com.amazonaws.services.rekognition.model.Smile;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DetectFaces_lib {
	AmazonRekognition rekognitionClient;
	DetectFacesRequest request;
	int id; //detectfacesテーブルのid

	public DetectFaces_lib(String bucket, String photo, int id) {
		rekognitionClient = AmazonRekognitionClientBuilder.standard()
				.withRegion("us-east-1")
                .build();
		request = new DetectFacesRequest()
				 .withImage(new Image()
				 .withS3Object(new S3Object()
				 .withName(photo)
				 .withBucket(bucket)))
				 .withAttributes(Attribute.ALL);
		this.id = id;
	}

	public void getDetectFaces() {
		int agelow, agehigh;
		String smile_value,glasses_value, gender_value;
		try {
			DetectFacesResult result = rekognitionClient.detectFaces(request);
			List < FaceDetail > faceDetails = result.getFaceDetails();
			for (FaceDetail face: faceDetails) {
				if (request.getAttributes().contains("ALL")) {
					AgeRange ageRange = face.getAgeRange();
					System.out.println("The detected face is estimated to be between "
							+ ageRange.getLow().toString() + " and " +
							ageRange.getHigh().toString()
							+ " years old.");
					//値取得
					agelow = Integer.parseInt(ageRange.getLow().toString());
					agehigh = Integer.parseInt(ageRange.getHigh().toString());
					Smile smile = face.getSmile();
					smile_value = smile.getValue().toString();
					Eyeglasses eyeglasses = face.getEyeglasses();
					glasses_value = eyeglasses.getValue().toString();
					//genderをそのまま入力しようとしたらエラーになったので、数値で入力
					//ダブルクオートの影響？前にもありました。
					Gender gender = face.getGender();
					gender_value = gender.getValue().toString();
					int gender_number = -1;
					if(gender_value.equals("Male")) {
						gender_number = 0;
						System.out.println("Male OK");
					}
					if(gender_value.equals("Female")) {
						gender_number = 1;
						System.out.println("Female OK");
					}
					double angry = -1, confused = -1, fear = -1, calm = -1, disgusted = -1, sad = -1, happy = -1, surprised = -1;
					
					List<Emotion> emotion = face.getEmotions();
					for(Emotion em: emotion) {
						String type = em.getType();
						double confidence = em.getConfidence();
						System.out.println("type = "+type+", confidence = "+confidence);
						if(type.equals("ANGRY")) angry = confidence;
						if(type.equals("CONFUSED")) confused = confidence;
						if(type.equals("FEAR")) fear = confidence;
						if(type.equals("CALM")) calm = confidence;
						if(type.equals("DISGUSTED")) disgusted = confidence;
						if(type.equals("SAD")) sad = confidence;
						if(type.equals("HAPPY")) happy = confidence;
						if(type.equals("SURPRISED")) surprised = confidence;
					}
					
					//残りの作業
					//感情の部分の入力
					//複数人の場合(テーブルを分けた方がよかったかも)
					
					MySQL mysql = new MySQL();
					mysql.updateDetectFaces(id, agelow, agehigh, smile_value, glasses_value, gender_number, happy, angry, confused, calm, surprised, sad, disgusted, fear);
					System.out.println("gender = "+gender_value);
					System.out.println("Here's the complete set of attributes:");
				 } else { // non-default attributes have null values.
					 System.out.println("Here's the default set of attributes:");
				 }
				 ObjectMapper objectMapper = new ObjectMapper();

				System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(face));
				
			}
		} catch (AmazonRekognitionException e) {
			e.printStackTrace();
		}
		catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
