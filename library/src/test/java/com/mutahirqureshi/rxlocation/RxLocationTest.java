package com.mutahirqureshi.rxlocation;

import android.support.v4.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareOnlyThisForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;


import static org.junit.Assert.*;

@RunWith(PowerMockRunner.class)
@PrepareOnlyThisForTest({ Observable.class, ContextCompat.class, LocationServices.class,ActivityRecognition.class,
    Places.class, Status.class, ConnectionResult.class, RxLocationBaseOnSubscribe.class })
public class RxLocationTest extends BaseOnSubscribeTest {

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        super.setup();
    }

    // RxFit

    @Test
    public void setTimeout() {
        rxLocation.setDefaultTimeout(TIMEOUT_TIME, TIMEOUT_TIMEUNIT);
        assertEquals(TIMEOUT_TIME, (long) rxLocation.timeoutTime);
        assertEquals(TIMEOUT_TIMEUNIT, rxLocation.timeoutUnit);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setTimeout_TimeUnitMissing() {
        rxLocation.setDefaultTimeout(TIMEOUT_TIME, null);
        assertNull(rxLocation.timeoutTime);
        assertNull(rxLocation.timeoutUnit);
    }

    @Test
    public void resetDefaultTimeout() {
        rxLocation.setDefaultTimeout(TIMEOUT_TIME, TIMEOUT_TIMEUNIT);
        rxLocation.resetDefaultTimeout();
        assertNull(rxLocation.timeoutTime);
        assertNull(rxLocation.timeoutUnit);
    }

    // GoogleApiClientObservable

    @Test
    public void GoogleAPIClientObservable_Success() {
        GoogleApiClientFlowable single = PowerMockito.spy(new GoogleApiClientFlowable(ctx, new Api[]{}, new Scope[]{}));

        setUpBaseFlowableSuccess(single);

        Flowable.create(single, BackpressureStrategy.LATEST).test().assertValue(apiClient);
    }

    @Test
    public void GoogleAPIClientObservable_ConnectionException() {
        final GoogleApiClientFlowable single = PowerMockito.spy(new GoogleApiClientFlowable(ctx, new Api[]{}, new Scope[]{}));

        setUpBaseFlowableError(single);

        assertError(Flowable.create(single, BackpressureStrategy.LATEST).test(), GoogleApiConnectionException.class);
    }
}
