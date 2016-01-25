/**
 * Licensed to Apereo under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Apereo licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jasig.portal.portlet.dao.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.EmbeddedId;

import org.apache.commons.lang3.builder.ToStringBuilder;

import org.jasig.portal.portlet.marketplace.IMarketplaceRating;

@Entity
@Table(name = "UP_PORTLET_RATING")
class MarketplaceRatingImpl implements IMarketplaceRating{


	@EmbeddedId
	private MarketplaceRatingPK marketplaceRatingPK;

	@Column(name="RATING")
	private int rating;
	
	@Column(name="REVIEW", length= REVIEW_MAX_LENGTH)
	private String review;

	@Override
	public MarketplaceRatingPK getMarketplaceRatingPK() {
		return marketplaceRatingPK;
	}

	@Override
	public void setMarketplaceRatingPK(MarketplaceRatingPK marketplaceRatingPK) {
		this.marketplaceRatingPK = marketplaceRatingPK;
	}

    @Override
    public String getReview() {
        return review;
    }

    @Override
    public void setReview(String review) {
        if(review != null){
            review = review.trim().substring(0, Math.min(review.length(), REVIEW_MAX_LENGTH));
        }
        this.review = review;
    }


	/**
	 * @return the rating
	 */
	public int getRating() {
		return rating;
	}

	/**
	 * @param rating must be within range of MAX_RATING and MIN_RATING
	 *        
	 */
	public void setRating(int rating) {
	    if(rating>MAX_RATING || rating < MIN_RATING){
	        throw new IllegalArgumentException();
	    }
		this.rating = rating;
	}

    @Override
    public String toString(){
        return new ToStringBuilder(this).
            append("RatingPK: ", this.marketplaceRatingPK).
            append("Rating: ", this.rating).
            append("Review: ", this.review).
            toString();
    }

}
