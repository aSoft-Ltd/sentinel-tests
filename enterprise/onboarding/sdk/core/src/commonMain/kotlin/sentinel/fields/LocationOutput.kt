package sentinel.fields

import geo.GeoLocation

interface LocationOutput {
    var location: GeoLocation?
}