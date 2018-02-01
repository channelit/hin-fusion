package biz.channelit.graph.hin.foursquare;


public class LoationInfo {

//    public Result<Recommended> venuesExplore(String ll, Double llAcc, Double alt, Double altAcc, Integer radius, String section, String query, Integer limit, String basis) throws FoursquareApiException {
//        try {
//            ApiRequestResponse response = doApiRequest(Method.GET, "venues/explore", isAuthenticated(), "ll", ll, "llAcc", llAcc, "alt", alt, "altAcc", altAcc, "radius", radius, "section", section, "query", query, "limit", limit, "basis", basis);
//            Recommended result = null;
//
//            if (response.getMeta().getCode() == 200) {
//                KeywordGroup keywords = (KeywordGroup) JSONFieldParser.parseEntity(KeywordGroup.class, response.getResponse().getJSONObject("keywords"), this.skipNonExistingFields);
//                RecommendationGroup[] groups = (RecommendationGroup[]) JSONFieldParser.parseEntities(RecommendationGroup.class, response.getResponse().getJSONArray("groups"), this.skipNonExistingFields);
//                Warning warning = response.getResponse().has("warning") ? (Warning) JSONFieldParser.parseEntity(Warning.class, response.getResponse().getJSONObject("warning"), this.skipNonExistingFields) : null;
//                result = new Recommended(keywords, groups, warning);
//            }
//
//            return new Result<Recommended>(response.getMeta(), result);
//        } catch (JSONException e) {
//            throw new FoursquareApiException(e);
//        }
//    }



}
