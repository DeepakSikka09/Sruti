package in.ecomexpress.sruti.repo.remote;

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import in.ecomexpress.sruti.utils.common_files.Constants;
import in.ecomexpress.sruti.utils.common_files.GlobalConstant;
import okhttp3.ResponseBody;
import retrofit2.HttpException;

public class RestApiErrorHandler {

    private static final String TAG = RestApiErrorHandler.class.getSimpleName();
    private ErrorResponse errorResponse = new ErrorResponse();

    public RestApiErrorHandler(Throwable throwable) {
        if (throwable == null) {
            setErrorDetails(000, "unknown error");
            return;
        }

        if (throwable instanceof UnrecognizedPropertyException) {
            UnrecognizedPropertyException unrecognizedPropertyException = ((UnrecognizedPropertyException) throwable);
            String strError = unrecognizedPropertyException.getLocation().getSourceRef().toString();
            try {
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
                mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
                errorResponse = mapper.readValue(strError, ErrorResponse.class);
            } catch (IOException e) {
                e.printStackTrace();
                setErrorDetails(000, throwable.getMessage());
            }


        } else if (throwable instanceof HttpException) {
            switch (((HttpException) throwable).code()) {
                case 200:
                    ResponseBody responseBody = ((HttpException) throwable).response().errorBody();
                    setErrorDetails(((HttpException) throwable).code(), ((HttpException) throwable).response().message());
                    break;
                case 404:
                    ResponseBody responseBody1 = ((HttpException) throwable).response().errorBody();
                    String erroeMsg = ((HttpException) throwable).response().message();
                    if (erroeMsg == null || erroeMsg.length() == 0) {
                        erroeMsg = Constants.ERROR_404;
                    }
                    erroeMsg = Constants.ERROR_404;
                    setErrorDetails(((HttpException) throwable).code(), erroeMsg);
                    break;
                default:
                    setErrorDetails(((HttpException) throwable).code(), throwable.getMessage());
                    break;
            }
        } else {
            //TODO
            Log.e(TAG, "RestApiErrorHandler: " + throwable.getMessage());
            setErrorDetails(GlobalConstant.ErrorCodes.HTTP_EXCEPTION, throwable.getMessage());

        }
    }


    private void setErrorDetails(long code, String details) {

        errorResponse.setStatus(false);
//        errorResponse.getEResponse().setStatusCode(code);
//        errorResponse.getEResponse().setDescription(details);
        ErrorResponse.EResponse response = new ErrorResponse.EResponse();
        response.setStatusCode(code);
        response.setDescription(details);
        errorResponse.setEResponse(response);

    }

    public ErrorResponse getErrorDetails() {
        return errorResponse;
    }


    public void writeErrorLogs(long timeStamp, String s) {
        writeCrashes(timeStamp, s);
    }

    private void writeCrashes(long timeStamp, String e) {

    }

    private String getExceptionAsString(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}
