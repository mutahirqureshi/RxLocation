package com.mutahirqureshi.rxlocation;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLngBounds;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.PrepareOnlyThisForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import io.reactivex.Single;


import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@SuppressWarnings("MissingPermission")
@RunWith(PowerMockRunner.class)
@PrepareOnlyThisForTest({ LocationServices.class, LatLngBounds.class, com.google.android.gms.location.ActivityRecognition.class,
    Places.class, Status.class, ConnectionResult.class, RxLocationBaseOnSubscribe.class
})
@PrepareForTest({ PlaceBuffer.class })
public class PlaceByIdOnSubscribeTest extends BaseOnSubscribeTest {

    @Mock PlaceBuffer placeBuffer;
    final String[] placeIds = new String[] { "some place id" };

    @Override
    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        super.setup();
        doReturn(status).when(placeBuffer).getStatus();
    }

    // PlaceIdSingle

    @Test
    public void PlaceByIdSingle_Success() {
        PlaceByIdSingleOnSubscribe single = PowerMockito.spy(new PlaceByIdSingleOnSubscribe(rxLocation, placeIds, null, null));

        setPendingResultValue(placeBuffer);
        when(status.isSuccess()).thenReturn(true);
        when(geoDataApi.getPlaceById(apiClient, placeIds)).thenReturn(pendingResult);

        setUpBaseSingleSuccess(single);

        assertSingleValue(Single.create(single).test(), placeBuffer);
    }

    @Test
    public void PlaceByIdSingle_StatusException() {
        PlaceByIdSingleOnSubscribe single = PowerMockito.spy(new PlaceByIdSingleOnSubscribe(rxLocation, placeIds, null, null));

        setPendingResultValue(placeBuffer);
        when(status.isSuccess()).thenReturn(false);
        when(geoDataApi.getPlaceById(apiClient, placeIds)).thenReturn(pendingResult);

        setUpBaseSingleSuccess(single);

        assertError(Single.create(single).test(), StatusException.class);
    }

}
